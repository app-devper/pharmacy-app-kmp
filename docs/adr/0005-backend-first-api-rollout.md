# Backend-first API rollout

Deploy backward-compatible backend behavior before a new KMP client depends on it. As an initial floor, support at least the two most recently released client versions and at least 90 days after a new release. Retire an old contract only after checking actual client usage or arranging updates for remaining installations. The application and backend have separate deployment paths, and web, mobile, and desktop clients can coexist at different versions; this order avoids requiring an atomic cross-repository release.
