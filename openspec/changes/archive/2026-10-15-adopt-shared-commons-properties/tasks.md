# Tasks — adopt-shared-commons-properties

One group, one implementer in a fresh context with this file, design.md and
`specs/ict-common-properties/spec.md`. Touch only the listed files, run only the listed
tests, tick the tasks, append a short `## Handoff — Group 1` note (what changed, what is
unverified) to `handoff.md` in this directory, and stop when the tests pass. Maven through `mvnq`
(`~/.local/bin/mvnq`, same arguments as `./mvnw`); never `clean`. No Java code specific to a
taxonomy class or property. Do not touch untracked IDE files.

Suggested model: sonnet (design pins every name, line and workflow step).

### Group 0: Issue and branch

- [x] 0.1 Open a GitHub issue in sirktek/sirk-ict-taxonomy ("Adopt shared commons properties:
      single ICT root, drop accounting opt-ins, manual Maven Central") linking
      sirktek/sirk-taxonomy-commons#1; put its number in proposal.md "GitHub issue".
- [x] 0.2 Branch `feature/#<id>-adopt-shared-commons-properties` from `main`.

### Group 1: Commons 3.14, ICT root, manual Central

Design D1–D5.

Files: `pom.xml`, `src/main/resources/taxonomy/ict-base.ttl`,
`src/main/java/no/sirktek/taxonomy/model/IctPropertyDefinition.java` (Javadoc only),
`src/test/java/no/sirktek/taxonomy/CommonPropertyOptInTest.java`,
`src/test/java/no/sirktek/taxonomy/TaxonomyServiceTest.java`,
`src/test/java/no/sirktek/taxonomy/model/PropertyDefinitionTest.java` (only if the bump breaks
it), `.github/workflows/build_tag_and_release.yml`,
new `.github/workflows/publish_maven_central.yml`, `README.md`.
Tests: `mvnq test -Dtest='CommonPropertyOptInTest,TaxonomyServiceTest'`

- [x] 1.1 Test first: rewrite `CommonPropertyOptInTest` on `IctTaxonomyService` (D3); replace
      `shouldExposeThreeTopLevelRoots` with `ictIsTheSingleRoot` and
      `hardwareSoftwareAndNetworkSitUnderIct`, make `allClassUrisAreInIctNamespace` recursive
      (D3). Bump `taxonomy-commons` to `3.14` in `pom.xml`. Run: the root tests fail, and the
      commons tests fail on `ledgerAccount` / `depreciationGroup` on Hardware and Software.
- [x] 1.2 `ict-base.ttl`: add `ict:ICT` and the three `rdfs:subClassOf ict:ICT` (D1); delete the
      accounting block (D2); fix the header comments (D1). Fix the `pom.xml` and
      `IctPropertyDefinition` comments (D2). Group tests pass.
- [x] 1.3 CI (D4): remove the Central steps from `build_tag_and_release.yml` and fix the
      SNAPSHOT message; add `publish_maven_central.yml`. Diff both against
      `/home/mkf/dev/sirk-taxonomy-commons/.github/workflows/` (and sirk-machine-taxonomy's, if
      landed) and keep only the D4 differences; `actionlint` if installed.
- [x] 1.4 README: structure and accounting paragraph (D5), "Releases" (D4).
- [x] 1.5 `mvnq test` once (full suite).

## Not in scope

Consumer edits (orgadmin-backend `TaxonomyServiceProducer` root list, orgadmin-frontend menu
and i18n, mcp-server tool text): see proposal.md "Impact". No commit, push or PR inside the
group unless asked.
