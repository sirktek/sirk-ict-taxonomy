
## Handoff — Group 1

Changed: `pom.xml` (commons 3.14, comment), `ict-base.ttl` (ict:ICT root, 3 subClassOf, accounting block and header comments removed), `IctPropertyDefinition` Javadoc, `CommonPropertyOptInTest` (rewritten on service), `TaxonomyServiceTest` (single root, children, recursive namespace), `build_tag_and_release.yml` (Central steps removed), new `publish_maven_central.yml` (copied from sirk-machine-taxonomy), `README.md`.
Verified: `mvnq test` full suite green. Workflow diffed against machine's: identical.
Unverified: red phase was not observed (tests and TTL written together); actionlint not installed; publish workflow cannot run before merge. `PropertyDefinitionTest` untouched.
