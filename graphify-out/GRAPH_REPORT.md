# Graph Report - .  (2026-09-28)

## Corpus Check
- Corpus is ~6,105 words - fits in a single context window. You may not need a graph.

## Summary
- 83 nodes · 123 edges · 12 communities (4 shown, 8 thin omitted)
- Extraction: 97% EXTRACTED · 3% INFERRED · 0% AMBIGUOUS · INFERRED: 4 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Community Hubs (Navigation)
- [[_COMMUNITY_Community 0|Community 0]]
- [[_COMMUNITY_Community 1|Community 1]]
- [[_COMMUNITY_Community 2|Community 2]]
- [[_COMMUNITY_Community 3|Community 3]]
- [[_COMMUNITY_Community 4|Community 4]]
- [[_COMMUNITY_Community 5|Community 5]]
- [[_COMMUNITY_Community 6|Community 6]]
- [[_COMMUNITY_Community 7|Community 7]]
- [[_COMMUNITY_Community 8|Community 8]]

## God Nodes (most connected - your core abstractions)
1. `ContactosFragment` - 15 edges
2. `FavoritosFragment` - 15 edges
3. `fragment_nuevo_contacto` - 7 edges
4. `ContactStorage` - 6 edges
5. `GruposFragment` - 6 edges
6. `RecientesFragment` - 6 edges
7. `ViewPagerAdapter` - 5 edges
8. `Contact` - 4 edges
9. `MainActivity` - 4 edges
10. `ExampleInstrumentedTest` - 2 edges

## Surprising Connections (you probably didn't know these)
- `ContactosFragment` --extends--> `Fragment`  [EXTRACTED]
  app/src/main/java/com/example/contactos/ContactosFragment.java →   _Bridges community 1 → community 0_
- `FavoritosFragment` --extends--> `Fragment`  [EXTRACTED]
  app/src/main/java/com/example/contactos/FavoritosFragment.java →   _Bridges community 0 → community 2_
- `fragment_nuevo_contacto` --extends--> `Fragment`  [EXTRACTED]
  app/src/main/java/com/example/contactos/fragment_nuevo_contacto.java →   _Bridges community 0 → community 4_

## Communities (12 total, 8 thin omitted)

### Community 0 - "Community 0"
Cohesion: 0.0
Nodes (4): ContactData, GruposFragment, RecientesFragment, Fragment

## Knowledge Gaps
- **8 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.