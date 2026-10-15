# ict-common-properties Specification

## Purpose
TBD - created by archiving change adopt-shared-commons-properties. Update Purpose after archive.
## Requirements
### Requirement: ICT has a single root class

The ICT taxonomy SHALL have exactly one root class, `ict:ICT`, labelled "ICT" in English and
"IKT" in Norwegian. `ict:Hardware`, `ict:Software` and `ict:Network` SHALL be its direct
subclasses, keeping their URIs and labels.

#### Scenario: One root
- **WHEN** `IctTaxonomyService` loads the base taxonomy
- **THEN** `rootCategories()` contains exactly one category, with `className` `ICT`, `englishName` `ICT` and `norwegianName` `IKT`

#### Scenario: Former roots under ICT
- **WHEN** `IctTaxonomyService` reads `Hardware`, `Software` and `Network`
- **THEN** each has `parentClassName` `ICT`, is not a root, and appears among the `ICT` category's children

#### Scenario: Deeper classes keep their parents
- **WHEN** `IctTaxonomyService` reads `NetworkEquipment`
- **THEN** its `parentClassName` is `Hardware`

### Requirement: ICT classes carry no commons properties

The ICT taxonomy SHALL NOT opt any ICT class into a commons property; in particular no
accounting property.

#### Scenario: Only ICT properties
- **WHEN** `IctTaxonomyService` loads the base taxonomy and every category is visited
- **THEN** every property's `uri` starts with `http://taxonomy.sirktek.no/ict#`

#### Scenario: No accounting on ICT
- **WHEN** `IctTaxonomyService` loads the base taxonomy and every category is visited
- **THEN** no category has any of the properties `ledgerAccount`, `depreciationGroup`, `valuations`, `bookValue`, `taxValue`, `wealthTaxValue`

### Requirement: Maven Central publishing is manual

A merge to `main` SHALL tag `3.<run number>` and publish to GitHub Packages only. Publishing a
release tag to Maven Central SHALL be a separate `workflow_dispatch` workflow taking the tag as
input.

#### Scenario: Merge to main
- **WHEN** a commit is pushed to `main`
- **THEN** `build_tag_and_release.yml` tags and deploys to GitHub Packages and runs no Maven Central step

#### Scenario: Manual Central release
- **WHEN** "Publish release to Maven Central" is run with input `tag` `3.17`
- **THEN** the workflow checks out `3.17`, resolves taxonomy-commons from GitHub Packages, sets version `3.17` and deploys with the `deployment` profile to Maven Central

