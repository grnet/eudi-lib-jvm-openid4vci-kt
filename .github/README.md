# EUDI OpenID4VCI library, GRNET fork

A fork of [eudi-lib-jvm-openid4vci-kt](https://github.com/eu-digital-identity-wallet/eudi-lib-jvm-openid4vci-kt)
for GRNET's wallet, through GRNET's fork of
[eudi-lib-android-wallet-core](https://github.com/grnet/eudi-lib-android-wallet-core).
Upstream's own README is [at the root](../README.md).

## Branches

| Branch | What |
| --- | --- |
| `grnet` | upstream's release it is based on, plus the changes below; releases are tagged here |
| `main` | an earlier sync with upstream `main`, unchanged |
| `v0.9.3-SNAPSHOT`, `v0.9.3-SNAPSHOT1` | earlier experiments, kept for reference |

## What differs from upstream

`grnet` is upstream `v0.14.1` with its own version number, a release
workflow, and this change:

- **The credential response's `display` array is passed through**
  (`0.14.1-grnet.1`). The WE BUILD rulebook for SCA-Card (DPC) attestations,
  `rb-sca-card-dpc` §2.9 and §4.1, has the issuer deliver the card's display
  meta-data (alias, last four digits, card art, network branding) "in the
  `display` array of the OpenID4VCI credential response". OpenID4VCI 1.0 does
  not define that parameter, so upstream drops it. It is now parsed and handed
  to the wallet, unchanged and unvalidated, as `SubmissionOutcome.Success.display`.
  The data is unsigned: the wallet treats it as display only. Every change is
  marked `GRNET fork`.

## Versions

Versions look like `<upstream version>-grnet.<N>`, e.g. `0.14.1-grnet.1`: the
upstream release the build is based on, then GRNET's release counter on that
base. A published version is never replaced, so every change, however small,
gets a new `N`. After a rebase onto a new upstream release, `N` starts again
at 1.

The version is `version` in `gradle.properties`.

## Releases

Published to a Maven repository on GitHub Pages:

    https://grnet.github.io/eudi-lib-jvm-openid4vci-kt/maven/

The artifact keeps upstream's coordinates, `eu.europa.ec.eudi:eudi-lib-jvm-openid4vci-kt`.
Downloading it needs no login. It is not signed, because signing is a Maven
Central requirement and the keys are upstream's.

To release, set `version`, commit, and push a matching tag:

    git tag v0.14.1-grnet.1
    git push origin v0.14.1-grnet.1

`.github/workflows/grnet-release.yml` checks that the tag matches `version`,
refuses a version that is already published, builds, and adds the version to
the `gh-pages` branch next to the earlier ones. It adds the repository through
a Gradle init script, `.github/grnet/pages-repository.init.gradle`, so the build
itself is unchanged. GitHub Pages has to be enabled once for the `gh-pages`
branch, after the first release creates it.

To rehearse a release locally, into a directory of your choice:

    GRNET_MAVEN_DIR=/tmp/grnet-maven ./gradlew \
        --init-script .github/grnet/pages-repository.init.gradle \
        publishAllPublicationsToGrnetPagesRepository \
        -PRELEASE_SIGNING_ENABLED=false -PsignAllPublications=false \
        --no-configuration-cache

## Using a release

GRNET's wallet-core takes `*-grnet.N` versions of this artifact from this
repository only, through an `exclusiveContent` block, and pins the version in
its version catalog. The wallet app does the same, since the artifact reaches
it through wallet-core.
