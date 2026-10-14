# Design — adopt-shared-commons-properties

## Precedent

- **Single root:** `furniture:Furniture` ("Furniture"/"Møbler", "Root class for all furniture
  items") and `machine:Machine` ("Machine"/"Maskin", "Root class for all machines and
  equipment, …") are each their taxonomy's only root; every other class is a subclass.
  `ict:ICT` follows them. Hardware, Software and Network keep their labels and become its
  children by one `rdfs:subClassOf` triple each.
- **Opt-ins:** a domain opts a class into a commons property with one
  `common:<p> schema:domainIncludes ict:<Class> .` triple in its own TTL, in the
  "Cross-cutting property domain extensions" section of `ict-base.ttl` (lines 243-264 today).
  ICT keeps none, so the section goes.
- **Tests through the service:** `TaxonomyServiceTest` asserts through `IctTaxonomyService`.
  `CommonPropertyOptInTest` checked a raw Jena model only because the loader did not attach
  commons properties; with 3.14 it follows `TaxonomyServiceTest` (as the machine taxonomy's
  change of the same name does).
- **CI:** commons PR #2 (commit `7edfcc7`): Central steps removed from
  `build_tag_and_release.yml`, `publish_maven_central.yml` on `workflow_dispatch` with input
  `tag`, README "Releases". Copied as sirk-machine-taxonomy's `adopt-shared-commons-properties`
  D3 does (this repo's `build_tag_and_release.yml` is identical to machine's).

No departure.

## Inventory of everything new or removed

| Kind | Item | Reader |
| --- | --- | --- |
| pom | `taxonomy-commons` 3.13 → 3.14 | build |
| TTL class | `ict:ICT` (root) | loader, orgadmin-backend (after its edit), test |
| TTL | `rdfs:subClassOf ict:ICT` on Hardware, Software, Network | loader, test |
| TTL removed | 8 opt-ins (`ledgerAccount`, `bookValue`, `taxValue`, `depreciationGroup` × Hardware, Software); "No wealthTaxValue" comment | — |
| Workflow | `.github/workflows/publish_maven_central.yml` | run by hand |
| Workflow removed | Central steps in `build_tag_and_release.yml` | — |
| Docs | README structure, "Releases"; accounting paragraph removed | — |

No new property, dependency, config or Java constant.

## Inventory: what each class gains on 3.14

`common:` opt-ins in `ict-base.ttl` today are only the eight accounting ones (lines 257-264).
`ict:manufacturer` / `ict:model` are ICT properties whose range is `common:Manufacturer` /
`common:Model`; commons declares no individuals of those classes, so their `enumValues` stay
empty and `IctPropertyDefinition` maps them to `CATEGORY` as before. Commons properties with a
commons domain (`address`, `emissionPerUnit` on Model, …) and the commercial terms (no domain)
attach to no ICT class; commons classes are outside the ICT namespace and so not in the tree.

| Classes | commons 3.13 (today) | 3.14, TTL unchanged | 3.14, this change |
| --- | --- | --- | --- |
| Hardware, Software (and subclasses by inheritance in consumers) | nothing | `ledgerAccount`, `depreciationGroup` (`privateSet accounting`); `bookValue`, `taxValue` inert (deleted in commons) | nothing |
| Network, `ICT`, every other class | nothing | nothing | nothing |

## Decisions

### D1 — Root class

After the "Root Classes" header (line 17), before `ict:Hardware`:

```turtle
ict:ICT a rdfs:Class ;
    rdfs:label "ICT"@en, "IKT"@no ;
    rdfs:comment "Root class for all ICT assets"@en .
```

Add `rdfs:subClassOf ict:ICT ;` as the first predicate line of `ict:Hardware`, `ict:Software`
and `ict:Network` (the style of the subclass blocks below them). Header becomes "Root Class";
the Hardware/Software/Network block gets its own "Top-level ICT subcategories" header.
File header (lines 2-3): "organized under one root, ICT, with Hardware, Software and a logical
Network grouping below it". Line 12: `# Cross-cutting classes (Manufacturer, Model, ...)`.

No other class lacks `rdfs:subClassOf` today (checked: Computer…SoftwareLicense all have an
ICT parent), so `ICT` is the only root. The loader takes the first in-namespace parent, so
category ids, URIs and `parentClassName` of every other class are unchanged;
`NetworkEquipment.parentClassName` stays `Hardware`. `ict:network`'s range stays `ict:Network`.

### D2 — Accounting opt-ins

Delete lines 243-264 (the "Cross-cutting property domain extensions" header block, the
wealthTaxValue and accounting comments and the eight triples). The file ends after
`ict:gateway`.

`pom.xml` lines 53-54 comment: "Sirktek Taxonomy Commons: Manufacturer/Model referenced by
ict:manufacturer / ict:model; its loader merges common-base.ttl." `IctPropertyDefinition`
Javadoc: the example becomes `common:AssetValuationEntry`.

### D3 — Tests

`CommonPropertyOptInTest` is rewritten on `IctTaxonomyService`, walking every category of
`loadBaseTaxonomy()` recursively:

- `noIctClassCarriesACommonsProperty`: every property of every category has a `uri` starting
  with `http://taxonomy.sirktek.no/ict#` (pins the inventory: no accounting, no commercial,
  nothing from commons).
- `noIctClassCarriesAccountingProperties`: no category has a property named `ledgerAccount`,
  `depreciationGroup`, `valuations`, `bookValue`, `taxValue` or `wealthTaxValue` (readable
  failure if an opt-in returns).

The two raw-Jena tests are deleted.

`TaxonomyServiceTest`:

- `shouldExposeThreeTopLevelRoots` is replaced by `ictIsTheSingleRoot`: `rootCategories()` has
  one entry, `className` `ICT`, `englishName` `ICT`, `norwegianName` `IKT`; and
  `hardwareSoftwareAndNetworkSitUnderIct`: each has `parentClassName` `ICT`, is not root, and
  is among `ICT`'s `children`.
- `allClassUrisAreInIctNamespace` walks the tree recursively (it now sees one root only).
- The rest unchanged. `PropertyDefinitionTest` changes only if the bump breaks a builder call.

### D4 — CI

As sirk-machine-taxonomy `adopt-shared-commons-properties` D3, verbatim:

- `build_tag_and_release.yml`: delete the "Publish to Maven Central" comment, the second
  `setup-java` (server `central`) and the Central deploy step; the SNAPSHOT guard message becomes
  "Cannot release a SNAPSHOT version". The GitHub-server `setup-java` stays.
- `publish_maven_central.yml`: commons' file, adapted because this repo resolves
  taxonomy-commons from GitHub Packages and `setup-java` writes one server into settings.xml:
  1. checkout `ref: ${{ inputs.tag }}`;
  2. `setup-java` with `server-id: github` (as the release workflow);
  3. "Resolve dependencies from GitHub Packages": `./mvnw -U compile --batch-mode
     --no-transfer-progress` with `GITHUB_ACTOR` / `GITHUB_TOKEN`;
  4. `setup-java` with `server-id: central` and GPG (commons' step verbatim; the local
     repository keeps taxonomy-commons);
  5. "Set Release Version" and "Publish to Maven Central" as commons.
  Header comment and the input description (`e.g. 3.17`) as commons; `permissions: contents:
  read, packages: read`. If machine's file has landed, copy it and diff.
- README "Releasing" becomes "Releases": commons' text, naming sirk-orgadmin-backend as the
  GitHub Packages consumer, plus: a Central release needs the taxonomy-commons version it
  depends on published to Central first.

### D5 — README

Opening line: "organized under one root, **ICT** (IKT), with **Hardware**, **Software** and a
logical **Network** grouping below it". Structure tree: `ict:ICT "ICT" / "IKT" (root)` with the
three subtrees indented under it (Hardware, Software, Network no longer marked root). Delete
the sentence on cross-cutting accounting properties; "Depends on" names Manufacturer/Model
only. Java API example unchanged.

## Risks

- **orgadmin-backend must list `ICT`** when it bumps to this release; otherwise
  `GET /taxonomies` keeps returning Hardware/Software/Network and the pickers are unchanged
  (no breakage, just no effect). Called out in proposal "Impact"; the edit is one line there.
- Shipping the bump without the TTL edit would expose accounting on Hardware/Software in
  orgadmin's tree: one group, one PR, one release.
- `publish_maven_central.yml` cannot be exercised before merge; a dry check is `actionlint` if
  installed, else YAML review against commons.
