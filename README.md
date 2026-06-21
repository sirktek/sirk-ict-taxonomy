# sirk-ict-taxonomy

RDF-S based **ICT (information & communications technology) taxonomy** with a Java API, organized under three roots: **Hardware**, **Software** and a logical **Network** grouping.

Extracted from `sirk-machine-taxonomy` in the 3.x line so IT assets live in their own domain taxonomy rather than under `machine:`.

## Structure

```
ict:Hardware   "Hardware" / "Maskinvare"     (root)
├─ ict:Computer                → Desktop, Laptop, Server   (covers servers too)
├─ ict:NetworkEquipment        → Router, Switch, AccessPoint, Firewall   ("network equipment" — physical devices)
└─ ict:Peripheral              → Monitor, Printer

ict:Software   "Software" / "Programvare"    (root)
├─ ict:Application
├─ ict:OperatingSystem
└─ ict:SoftwareLicense

ict:Network    "Network" / "Nettverk"        (root)
   a logical network grouping (NOT a device) — tenants create org-specific
   subcategories ("HQ Network") that equipment belongs to
```

`ict:Network` is deliberately **separate from `ict:NetworkEquipment`**: the former is a logical grouping (tenants create org-specific subcategories under it to document a network and its layout — `cidr`/VLAN/gateway — in one place); the latter is the physical device type under Hardware. Hardware carries a multi-valued `network` property (`MULTI_CATEGORY`, range `ict:Network`) so a device can belong to several networks; membership is realized as the asset's category memberships filtered to the `ict:Network` subtree.

Shared properties (`manufacturer`, `model`, `purchaseDate`) span Hardware and Software via `schema:domainIncludes`. Cross-cutting `common:assetValue` (formuesverdi) is extended to `ict:Hardware` and `ict:Software`.

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
Optional<CategoryInfo> hardware = svc.getCategoryByClassName("Hardware");
Optional<CategoryInfo> network = svc.getCategoryByClassName("Network");
```

`IctPropertyDefinition.getPropertyType(...)` maps `ict:*` property ranges to typed values for consumers.

## Releasing

Pushing to `main` triggers `.github/workflows/build_tag_and_release.yml`, which tags `3.<run_number>` and dual-publishes to **GitHub Packages** and **Maven Central**. Maven Central requires the org secrets `MAVEN_GPG_PRIVATE_KEY`, `MAVEN_GPG_PASSPHRASE`, `MAVEN_CENTRAL_USERNAME`, `MAVEN_CENTRAL_TOKEN`.
