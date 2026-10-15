# Proposal — adopt-shared-commons-properties

## Why

taxonomy-commons 3.14 (sirk-taxonomy-commons PR #2, change `shared-commons-properties`) makes
`schema:domainIncludes` opt-ins work: every domain loader now reads `common-base.ttl` and
attaches commons properties a class opts into. Until now the eight opt-ins in `ict-base.ttl`
(four accounting properties on Hardware and Software) were dead triples. 3.14 also reshapes
accounting into ledger17's shape (`ledgerAccount`, `depreciationGroup`, `valuations`, marked
`common:privateSet "accounting"`) and deletes `common:bookValue` / `common:taxValue` /
`common:wealthTaxValue`, the `AssetValueEntry` marker and `ASSET_VALUE`.

Product owner decisions (2026-10-13, round 3): accounting opt-ins on ICT are deleted (they may
come back later, in accounting). Central's monthly publishing limit is reached, so Maven Central
becomes a manual step, as in commons PR #2.

Product owner decision (2026-10-13): ICT gets one root class, `ict:ICT` ("ICT" / "IKT"), as
furniture (`furniture:Furniture`, "Møbler") and machine (`machine:Machine`, "Maskin") have.
asset-frontend shows three ICT roots side by side with the other taxonomies' single roots, and
"Maskinvare" is confused with "Maskin".

GitHub issue: sirktek/sirk-ict-taxonomy#1.

## What changes

- **Bump** `no.sirktek:taxonomy-commons` 3.13 → 3.14.
- **`ict-base.ttl`:**
  - new root class `ict:ICT` (`"ICT"@en`, `"IKT"@no`, comment `"Root class for all ICT
    assets"@en`); the three current roots `ict:Hardware`, `ict:Software`, `ict:Network` (the only
    classes without `rdfs:subClassOf`) become `rdfs:subClassOf ict:ICT`. Labels unchanged.
  - delete the eight accounting opt-ins and the "No wealthTaxValue" comment; correct the header
    comments ("three roots", "bookValue" on the `common:` prefix).
- **Comments:** the `assetValue` comment on the commons dependency in `pom.xml` and the
  `common:AssetValueEntry` example in `IctPropertyDefinition`'s Javadoc.
- **Tests (through `IctTaxonomyService`):** `CommonPropertyOptInTest` asserts that no ICT class
  carries a commons property (accounting or other); `TaxonomyServiceTest` asserts `ICT` is the
  single root with Hardware, Software and Network as its children.
- **CI:** drop the Maven Central steps from `build_tag_and_release.yml`; new
  `publish_maven_central.yml` (`workflow_dispatch`, input `tag`), adapted from commons; README
  "Releasing" becomes "Releases".
- **README:** structure tree under `ict:ICT`; the accounting paragraph goes.

Release: next 3.x on merge. The bump and the TTL edit ship in one release: the bump alone would
attach `ledgerAccount` / `depreciationGroup` to Hardware and Software.

## Impact

- **Category ids** derive from URIs (`RdfsCategoryIds.negativeIdFromUri`). No URI changes, so
  every existing id is unchanged; `ict:ICT` gets a new id.
- **Existing assets** have no `ICT` id in their category lists (the picker adds ancestors at
  pick time; ICT did not exist). No backfill: nothing filters on an ICT root today, and the
  subtree filters that exist expand a root to its descendants (orgadmin
  `getSubsumedCategoryIds`, asset-frontend's client-side expansion), so a later filter on `ICT`
  still matches them. app-backend's `CategoryDescendantResolver` loads the furniture tree only.
- **sirk-orgadmin-backend** (on ict-taxonomy 3.5): `TaxonomyServiceProducer` hard-codes the ICT
  root names `List.of("Hardware", "Software", "Network")`, which `GET /taxonomies` returns as
  `rootClassNames` and asset-frontend, orgadmin-frontend and sirk-mcp-server read. In its bump
  PR (with commons 3.14, `private-property-definitions`) it becomes `List.of("ICT")`, and
  `TaxonomyResourceTest` line 52 follows. Without that edit the three roots stay in every
  picker. Its tree, URI and id lookups search recursively and need no change. After the bump
  its tree shows no accounting properties on Hardware/Software (they were inert before).
- **sirk-orgadmin-frontend:** hard-codes the three roots in `taxonomyMenuConfig.ts`
  (`MENU_ROOT_DISPLAY`), `AdminDashboard.tsx`, `config/apps/sirktek.ts` routes,
  `useAvailableTaxonomies.ts` fallback, i18n (`hardware`/`software`/`network`) and two tests.
  Once orgadmin-backend returns `ICT`, the menu shows one entry `ict` with a fallback label
  until it gets an `ict` display entry and i18n ("IKT"); the three entries become dead.
  Follow-up there.
- **sirk-asset-frontend, sirk-frontend-commons, vardly-frontend, sirk-app-backend:** nothing
  hard-codes an ICT root; asset-frontend picks up `ICT` from `rootClassNames`.
- **sirk-mcp-server:** serves `ict-base.ttl` as schema (pinned 3.4); one tool description uses
  `"Hardware"` as an example root name. Text follow-up, not blocking.
- No Java API change in this repo.

## Not in scope

- Moving `manufacturer` / `model` / `purchaseDate` from Hardware + Software to `ict:ICT`
  (Network would gain them).
- Same-named redeclarations across namespaces (`serialNumber`, `model`, `manufacturer`):
  follow-up, each needs a commons term.
- Commercial terms (`monthlyRent`, …) on ICT: not requested.
- The consumer edits listed under Impact (orgadmin-backend, orgadmin-frontend, mcp-server).
- Publishing this or any earlier release to Maven Central.

## Open questions for the product owner

None.
