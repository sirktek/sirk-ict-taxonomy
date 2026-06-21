# sirk-ict-taxonomy

RDF-S based **ICT (information & communications technology) taxonomy** with a Java API — IT equipment (computers, network equipment) plus a logical **Network** grouping root.

Extracted from `sirk-machine-taxonomy` in the 3.x line so IT assets live in their own domain taxonomy rather than under `machine:`.

## Structure

```
ict:InformationTechnology            (root)  "IT Equipment" / "IT-utstyr"
├─ ict:Computer (PC)                 → Desktop, Laptop, Server
├─ ict:NetworkEquipment              → Router, Switch, AccessPoint, Firewall   ("Network equipment")
└─ ict:Network                       a logical network grouping (NOT a device) — org-specific
                                       networks ("HQ Network") that equipment belongs to
```

`ict:Network` is deliberately **separate from `ict:NetworkEquipment`**: the former is a logical grouping (tenants create org-specific subcategories under it to document a network and its layout — subnet/VLAN/gateway — in one place); the latter is the physical device type.

Cross-cutting `common:assetValue` (formuesverdi) is extended to `ict:InformationTechnology`.

- **Namespace:** `http://taxonomy.sirktek.no/ict#`
- **Depends on:** `no.sirktek:taxonomy-commons` (Manufacturer/Model + assetValue)

## Build

```bash
./mvnw clean test
```

## Java API

```java
IctTaxonomyService svc = new IctTaxonomyService();
TaxonomyTree tree = svc.loadBaseTaxonomy();
Optional<CategoryInfo> network = svc.getCategoryByClassName("Network");
```

`IctPropertyDefinition.getPropertyType(...)` maps `ict:*` property ranges to typed values for consumers.

## Releasing

Pushing to `main` triggers `.github/workflows/build_tag_and_release.yml`, which tags `3.<run_number>` and dual-publishes to **GitHub Packages** and **Maven Central**. Maven Central requires the org secrets `MAVEN_GPG_PRIVATE_KEY`, `MAVEN_GPG_PASSPHRASE`, `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_TOKEN`.
