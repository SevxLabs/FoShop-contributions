# External pricing API

FoShopPriceApi allows one enabled plugin to register a FoShopPriceProvider. Obtain the API from FoShop#getPriceApi() or Bukkit's ServicesManager. Registration, invalidation, catalog changes and completed sale reports belong on the server thread. Pricing may also run on packet threads: callbacks must use immutable snapshots, perform no blocking I/O and avoid player scans.

The provider receives the original configured unit price and the offered unit price after existing FoShop bonus selection. Its result is the final unit price. Negative or nonfinite results deny the sale. quote(player, item, externalMultiplier) includes an external seller's multiplier before this callback; external sellers must not multiply the returned price again.

getOriginalBaseSellPrice preserves access to the unmodified configured value. Catalog changes notify the provider so it can invalidate stale buy/sell ceilings. API revision changes invalidate inventory-worth caches. Optional provider lore is included in existing shop, worth and rotating displays; administrative base-price editors retain original prices.

After inventory and payment commit, reportSale(transactionId, source, actualSoldStacks) emits a noncancellable FoShopSaleEvent. Failed payments must not report sales. Virtual material storage can use reportMaterialSale with long quantities. Reports are notifications, never permission to repeat or reverse a transaction. Keep retry IDs stable and do not replay historical sales after restart. A report accepts at most 8,192 stacks; callers should bound traversal before committing a sale.

Without a registered provider, existing pricing is unchanged. Disabling a provider owner unregisters it. Market algorithms, price history, limits and persistence remain addon responsibilities.

Validation: the changed source compiles with Java 21 and the inspected runtime dependencies using an external validation build. An integration fork using this API has passed server startup; exhaustive inventory/payment and packet-thread tests remain necessary. The repository's original Maven configuration and metadata are unchanged.
## Rotating selection

FoShopRotationEvent fires synchronously before selecting a new rotation. It exposes immutable eligible candidates (section ID, item ID, material) and the slot limit. A listener may choose a distinct eligible subset within that limit or leave selection null for ordinary random selection. FoShop retains multiplier assignment and shuffles the chosen entries. Cancellation retains existing offers and retries in five minutes. Do not perform blocking work in listeners. Category pools and quotas remain addon policy; this API contains no server-specific selection rules.
