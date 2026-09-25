# Explicit cross-tenant delegation

A SUPER role alone does not select or grant access to another pharmacy tenant. Pharmacy data remains scoped to the tenant named by the authenticated context. If cross-tenant support is required, it needs an explicit, auditable delegation to a named tenant rather than an implicit role-wide bypass. This preserves tenant isolation while allowing a controlled support workflow later.
