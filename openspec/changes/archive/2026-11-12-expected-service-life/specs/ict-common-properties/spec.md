## MODIFIED Requirements

### Requirement: ICT carries the shared, new-price and age commons properties

The ICT taxonomy SHALL opt `ict:Hardware` and `ict:Software` into `common:manufacturer` and
`common:model`, `common:newPriceExVAT` and `common:installationYear`, `ict:Hardware` only into
`common:serialNumber`, `common:condition`, `common:yearOfManufacture`, `common:expectedServiceLife`,
`common:weight` and `common:emissionPerUnit`, and no ICT class into any other commons property. Every other property on
an ICT class SHALL have an `ict#` IRI.

#### Scenario: Only ICT and the opted-in commons properties
- **WHEN** `IctTaxonomyService` loads the base taxonomy and every category is visited
- **THEN** every property's `uri` either starts with `http://taxonomy.sirktek.no/ict#` or is `http://taxonomy.sirktek.no/common#` followed by one of `manufacturer`, `model`, `serialNumber`, `condition`, `newPriceExVAT`, `yearOfManufacture`, `installationYear`, `expectedServiceLife`, `weight`, `emissionPerUnit`

#### Scenario: New price on Hardware
- **WHEN** `IctTaxonomyService` loads the base taxonomy and the Hardware category is read
- **THEN** it has one property `newPriceExVAT` with `uri` `http://taxonomy.sirktek.no/common#newPriceExVAT`, `rangeType` `http://www.w3.org/2001/XMLSchema#decimal` and `privateSet` `commercial`

#### Scenario: Age properties on Hardware
- **WHEN** `IctTaxonomyService` loads the base taxonomy and the Hardware category is read
- **THEN** it has one property `yearOfManufacture` with `uri` `http://taxonomy.sirktek.no/common#yearOfManufacture` and one `installationYear` with `uri` `http://taxonomy.sirktek.no/common#installationYear`, both with `rangeType` `http://www.w3.org/2001/XMLSchema#integer` and no `privateSet`

#### Scenario: Service life, weight and emission on Hardware
- **WHEN** `IctTaxonomyService` loads the base taxonomy and the Hardware category is read
- **THEN** it has one property each of `expectedServiceLife` (`rangeType` `http://www.w3.org/2001/XMLSchema#integer`), `weight` (`rangeType` `http://www.w3.org/2001/XMLSchema#decimal`) and `emissionPerUnit` (`rangeType` `http://taxonomy.sirktek.no/common#EmissionEntry`), each with a `http://taxonomy.sirktek.no/common#` `uri` and no `privateSet`

#### Scenario: New price and installation year on Software, no year of manufacture
- **WHEN** `IctTaxonomyService` loads the base taxonomy and the Software category is read
- **THEN** it has one property `newPriceExVAT` with `uri` `http://taxonomy.sirktek.no/common#newPriceExVAT` and `privateSet` `commercial`, one `installationYear` with `uri` `http://taxonomy.sirktek.no/common#installationYear`, `rangeType` `http://www.w3.org/2001/XMLSchema#integer` and no `privateSet`, and no property `yearOfManufacture`

#### Scenario: Software carries no service life, weight or emission
- **WHEN** `IctTaxonomyService` loads the base taxonomy and the Software category is read
- **THEN** it has no property `expectedServiceLife`, `weight` or `emissionPerUnit`

#### Scenario: No accounting on ICT
- **WHEN** `IctTaxonomyService` loads the base taxonomy and every category is visited
- **THEN** no category has any of the properties `ledgerAccount`, `depreciationGroup`, `valuations`, `bookValue`, `taxValue`, `wealthTaxValue`
