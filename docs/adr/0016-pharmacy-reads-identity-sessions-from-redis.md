# Pharmacy reads identity sessions from Redis

Supersedes the "without sharing the identity service's Redis" clause of [ADR-0014](./0014-bound-pharmacy-permission-staleness.md); its bounds still hold.

The pharmacy API confirms a token's session by reading the identity service's session key from Redis rather than calling a verification endpoint. Because Redis holds no role or tenant, the identity service revokes every session of a user whose role, status, password, or account changes, and the pharmacy API authorizes from the signed token's claims only while that session exists. The session key layout becomes a contract owned by the identity service. The pharmacy API reads it and never writes it.
