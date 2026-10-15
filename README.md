# sirk-ict-taxonomy

RDF-S based **ICT (information & communications technology) taxonomy** with a Java API, organized under one root, **ICT** (IKT), with **Hardware**, **Software** and a logical **Network** grouping below it.

Extracted from `sirk-machine-taxonomy` in the 3.x line so IT assets live in their own domain taxonomy rather than under `machine:`.

## Structure

```
ict:ICT        "ICT" / "IKT"                 (root)
├─ ict:Hardware   "Hardware" / "Maskinvare"
│  ├─ ict:Computer             → Desktop, Laptop, Server   (covers servers too)
│  ├─ ict:NetworkEquipment     → Router, Switch, AccessPoint, Firewall   ("network equipment" — physical devices)
│  └─ ict:Peripheral           → Monitor, Printer
├─ ict:Software   "Software" / "Programvare"
│  ├─ ict:Application
│  ├─ ict:OperatingSystem
│  └─ ict:SoftwareLicense
└─ ict:Network    "Network" / "Nettverk"
   a logical network grouping (NOT a device) — tenants create org-specific
   subcategories ("HQ Network") that equipment belongs to
```

`ict:Network` is deliberately **separate from `ict:NetworkEquipment`**: the former is a logical grouping (tenants create org-specific subcategories under it to document a network and its layout — `cidr`/VLAN/gateway — in one place); the latter is the physical device type under Hardware. Hardware carries a multi-valued `network` property (`MULTI_CATEGORY`, range `ict:Network`) so a device can belong to several networks; membership is realized as the asset's category memberships filtered to the `ict:Network` subtree.

`manufacturer`, `model` (both Hardware and Software) and `serialNumber` (Hardware) are declared in commons ("Shared asset properties") and opted in here via `schema:domainIncludes`. `purchaseDate` stays ICT and spans Hardware and Software.

- **Namespace:** `http://taxonomy.sirktek.no/ict#`
- **Depends on:** `no.sirktek:taxonomy-commons` 3.19 (Manufacturer/Model and the shared asset properties)

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

## Releases

Every merge to `main` tags `3.<run number>` and publishes it to GitHub Packages, where
sirk-orgadmin-backend consumes it. Maven Central (monthly publishing limit) is manual: run
"Publish release to Maven Central" with the tag when a Central consumer needs that version. A
Central release needs the `taxonomy-commons` version it depends on published to Central first.
