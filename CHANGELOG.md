## [4.1.0](https://github.com/Basis-Theory/basistheory-android/compare/4.0.1...4.1.0) (2024-02-02)


### Features

* expose autofillHints from the edit text within the text element ([#100](https://github.com/Basis-Theory/basistheory-android/issues/100)) ([0c68b93](https://github.com/Basis-Theory/basistheory-android/commit/0c68b93f851899a4bd49269059e569f5c9340864))


### [4.0.1](https://github.com/Basis-Theory/basistheory-android/compare/4.0.0...4.0.1) (2024-02-01)


### Bug Fixes

* handle revealing january expiration month as a single digit ([#99](https://github.com/Basis-Theory/basistheory-android/issues/99)) ([bf61540](https://github.com/Basis-Theory/basistheory-android/commit/bf61540e0c7e81a5302a00bafd08421909bed806))


## [4.0.0](https://github.com/Basis-Theory/basistheory-android/compare/3.2.0...4.0.0) (2024-01-30)


### ⚠ BREAKING CHANGES

* removing dependency on java sdk models for consumers

### Features

* remove java sdk dependency for consumers ([#98](https://github.com/Basis-Theory/basistheory-android/issues/98)) ([3ed978a](https://github.com/Basis-Theory/basistheory-android/commit/3ed978af38f74f65f6124460893f7146ef1700f0))


## [3.2.0](https://github.com/Basis-Theory/basistheory-android/compare/3.1.0...3.2.0) (2023-10-31)


### Features

* updating dependencies and library tools/languages versions ([#96](https://github.com/Basis-Theory/basistheory-android/issues/96)) ([9433d32](https://github.com/Basis-Theory/basistheory-android/commit/9433d32b8bee1ee2434c7284d88f41730a39a86b))


## [3.1.0](https://github.com/Basis-Theory/basistheory-android/compare/3.0.0...3.1.0) (2023-09-15)


### Features

* add enableCopy option and getDrawables to TextElement ([#94](https://github.com/Basis-Theory/basistheory-android/issues/94)) ([87a8404](https://github.com/Basis-Theory/basistheory-android/commit/87a8404c9aabe67228ed20e8b20039cbef6f00ab))


## [3.0.0](https://github.com/Basis-Theory/basistheory-android/compare/2.5.0...3.0.0) (2023-09-12)


### ⚠ BREAKING CHANGES

* the value for the month() and year() methods of CardExpirationDateElement is now converted to Int before sending to the API to match the APIs 'card' contract instead of String as it was before.

### Features

* add type conversion when submitting element values ([#93](https://github.com/Basis-Theory/basistheory-android/issues/93)) ([961d5e3](https://github.com/Basis-Theory/basistheory-android/commit/961d5e32f8e4eae6ada3f91a4423eafefb6ebcc6))


## [2.5.0](https://github.com/Basis-Theory/basistheory-android/compare/2.4.0...2.5.0) (2023-09-04)


### Features

* add gravity attribute to TextElement ([#92](https://github.com/Basis-Theory/basistheory-android/issues/92)) ([110fe97](https://github.com/Basis-Theory/basistheory-android/commit/110fe97b71d8502ee0438567c7e4701ce0e826f8))


## [2.4.0](https://github.com/Basis-Theory/basistheory-android/compare/2.3.0...2.4.0) (2023-07-20)


### Features

* updating dependencies ([#85](https://github.com/Basis-Theory/basistheory-android/issues/85)) ([016ae3f](https://github.com/Basis-Theory/basistheory-android/commit/016ae3f22aa68639e5c69a2ccf0332eee8eb9719))


## [2.3.0](https://github.com/Basis-Theory/basistheory-android/compare/2.2.0...2.3.0) (2023-07-06)


### Features

* adds format fn on CardExpirationDate element ([#78](https://github.com/Basis-Theory/basistheory-android/issues/78)) ([6cc98f9](https://github.com/Basis-Theory/basistheory-android/commit/6cc98f935012b572876ea841e419a9b14baf1e1e))


## [2.2.0](https://github.com/Basis-Theory/basistheory-android/compare/2.1.2...2.2.0) (2023-06-15)


### Features

* adds bt http client ([#74](https://github.com/Basis-Theory/basistheory-android/issues/74)) ([d8d071c](https://github.com/Basis-Theory/basistheory-android/commit/d8d071ccc1039cdf1d9f5c577dd0173e55f26999))


### [2.1.2](https://github.com/Basis-Theory/basistheory-android/compare/2.1.1...2.1.2) (2023-06-05)


### Bug Fixes

* support kotlin 1.8.21 ([#73](https://github.com/Basis-Theory/basistheory-android/issues/73)) ([b998fb4](https://github.com/Basis-Theory/basistheory-android/commit/b998fb44e7f707f8c1436ca368d9371788e1f831))


### [2.1.1](https://github.com/Basis-Theory/basistheory-android/compare/2.1.0...2.1.1) (2023-05-31)


### Bug Fixes

* Mapping collections in proxy requests/responses to/from ElementValueReferences ([#71](https://github.com/Basis-Theory/basistheory-android/issues/71)) ([2d7e4e8](https://github.com/Basis-Theory/basistheory-android/commit/2d7e4e8bd51e6f07cf1efaa672f8adf1b565291f))


## [2.1.0](https://github.com/Basis-Theory/basistheory-android/compare/2.0.0...2.1.0) (2023-05-23)


### Features

* adds example fragment for proxy non-sensitive response ([#70](https://github.com/Basis-Theory/basistheory-android/issues/70)) ([8520f80](https://github.com/Basis-Theory/basistheory-android/commit/8520f8072a957eba84ce5dad56e554e0d1e10d6c))


## [2.0.0](https://github.com/Basis-Theory/basistheory-android/compare/1.14.0...2.0.0) (2023-05-18)


### ⚠ BREAKING CHANGES

* Custom XML attributes have been replaced with either standard android:* attributes or prefixed with bt_* to avoid collisions with other attributes declared in third party libraries

### Bug Fixes

* Replace and prefix custom attributes to prevent collisions ([#65](https://github.com/Basis-Theory/basistheory-android/issues/65)) ([9edb650](https://github.com/Basis-Theory/basistheory-android/commit/9edb6503bec82fa30cfaaf20faa89173f542f72d))


## [1.14.0](https://github.com/Basis-Theory/basistheory-android/compare/1.13.1...1.14.0) (2023-05-15)


### Features

* support arrays when tokenizing ([#63](https://github.com/Basis-Theory/basistheory-android/issues/63)) ([3bf1876](https://github.com/Basis-Theory/basistheory-android/commit/3bf18768155218438076fbf523408a1f3ac5803b))


### [1.13.1](https://github.com/Basis-Theory/basistheory-android/compare/1.13.0...1.13.1) (2023-05-12)


### Bug Fixes

* removing this properties when converting objects to map ([#62](https://github.com/Basis-Theory/basistheory-android/issues/62)) ([e2a9005](https://github.com/Basis-Theory/basistheory-android/commit/e2a9005bfffb890d80c7c19300d224b763bf30de))


## [1.13.0](https://github.com/Basis-Theory/basistheory-android/compare/1.12.0...1.13.0) (2023-05-05)


### Features

* set hint color and adding jetpack compose example ([#61](https://github.com/Basis-Theory/basistheory-android/issues/61)) ([4385482](https://github.com/Basis-Theory/basistheory-android/commit/4385482c2a4130f5ba50c88348c24379b1b283a7))


## [1.12.0](https://github.com/Basis-Theory/basistheory-android/compare/1.11.0...1.12.0) (2023-05-05)


### Features

* set text element drawables ([#60](https://github.com/Basis-Theory/basistheory-android/issues/60)) ([88201e0](https://github.com/Basis-Theory/basistheory-android/commit/88201e0a5910b2e6b639da2f8e8e3fcba3d1f9b0))


## [1.11.0](https://github.com/Basis-Theory/basistheory-android/compare/1.10.0...1.11.0) (2023-05-03)


### Features

* Adds typeface property to elements ([#59](https://github.com/Basis-Theory/basistheory-android/issues/59)) ([c00241c](https://github.com/Basis-Theory/basistheory-android/commit/c00241ca39d716d8fd0e327d8eb664d096f1d49b))


## [1.10.0](https://github.com/Basis-Theory/basistheory-android/compare/1.9.0...1.10.0) (2023-05-02)


### Features

* Adds support for hidden input types ([#58](https://github.com/Basis-Theory/basistheory-android/issues/58)) ([98b36f9](https://github.com/Basis-Theory/basistheory-android/commit/98b36f9b928f5826b51de7f0290db4ca10189473))


## [1.9.0](https://github.com/Basis-Theory/basistheory-android/compare/1.8.0...1.9.0) (2023-03-31)


### Features

* allow passing elements in proxy body ([#51](https://github.com/Basis-Theory/basistheory-android/issues/51)) ([aa92321](https://github.com/Basis-Theory/basistheory-android/commit/aa923215333978dc1034abb1e0ed5c898c05a97c))


## [1.8.0](https://github.com/Basis-Theory/basistheory-android/compare/1.7.0...1.8.0) (2023-03-29)


### Features

* updating card bin depending on card length ([#52](https://github.com/Basis-Theory/basistheory-android/issues/52)) ([6d6585b](https://github.com/Basis-Theory/basistheory-android/commit/6d6585b4ebc947f9f6492fdf604c939251001d5d))


## [1.7.0](https://github.com/Basis-Theory/basistheory-android/compare/1.6.0...1.7.0) (2023-03-27)


### Features

* do not allow invalid month inputs ([#48](https://github.com/Basis-Theory/basistheory-android/issues/48)) ([65841c0](https://github.com/Basis-Theory/basistheory-android/commit/65841c09fef46d918556b73835d0f6ee1ba01a7c))


## [1.6.0](https://github.com/Basis-Theory/basistheory-android/compare/1.5.0...1.6.0) (2023-01-26)


### Features

* support proxy and retrieve ([#40](https://github.com/Basis-Theory/basistheory-android/issues/40)) ([a0b3cea](https://github.com/Basis-Theory/basistheory-android/commit/a0b3cea9474e82e9ba94877acdcd6991306e25af))


## [1.5.0](https://github.com/Basis-Theory/basistheory-android/compare/1.4.0...1.5.0) (2023-01-10)


### Features

* Parity with elements on other platforms ([#32](https://github.com/Basis-Theory/basistheory-android/issues/32)) ([eea07e6](https://github.com/Basis-Theory/basistheory-android/commit/eea07e66bd63488e00ad3210c6d76d052fe288c3))


## [1.4.0](https://github.com/Basis-Theory/basistheory-android/compare/1.3.0...1.4.0) (2023-01-06)


### Features

* adds support for readonly text elements and value references ([#31](https://github.com/Basis-Theory/basistheory-android/issues/31)) ([f5901d8](https://github.com/Basis-Theory/basistheory-android/commit/f5901d897d4b7de787f10b4fab55a76fe2061889))


## [1.3.0](https://github.com/Basis-Theory/basistheory-android/compare/1.2.0...1.3.0) (2023-01-05)


### Features

* Publishes change event from CardVerificationCodeElement on brand changes ([#30](https://github.com/Basis-Theory/basistheory-android/issues/30)) ([6e8072a](https://github.com/Basis-Theory/basistheory-android/commit/6e8072a7c09e1c7388bd23b3834b0e519fa50f97))


## [1.2.0](https://github.com/Basis-Theory/basistheory-android/compare/1.1.0...1.2.0) (2023-01-05)


### Features

* Adds bin and last4 to change event details ([#29](https://github.com/Basis-Theory/basistheory-android/issues/29)) ([67c1ced](https://github.com/Basis-Theory/basistheory-android/commit/67c1ced6f2f007e9394533c40a8d27ff2e5a4c53))


## [1.1.0](https://github.com/Basis-Theory/basistheory-android/compare/1.0.0...1.1.0) (2022-12-21)


### Features

* support create token endpoint ([#23](https://github.com/Basis-Theory/basistheory-android/issues/23)) ([f441a5c](https://github.com/Basis-Theory/basistheory-android/commit/f441a5c4e67ab2ebe17229a293779e8d5ace36a6))


## [1.0.0](https://github.com/Basis-Theory/basistheory-android/compare/0.12.0...1.0.0) (2022-12-20)


### ⚠ BREAKING CHANGES

* releasing version 1.0.0

### Features

* Adds documentation for card elements ([#21](https://github.com/Basis-Theory/basistheory-android/issues/21)) ([ca82935](https://github.com/Basis-Theory/basistheory-android/commit/ca82935df420f39c371596b49f13327e9554c21c))


## [0.12.0](https://github.com/Basis-Theory/basistheory-android/compare/0.11.0...0.12.0) (2022-12-16)


### Features

* Adding user-agent header to http requests ([#22](https://github.com/Basis-Theory/basistheory-android/issues/22)) ([9ee9593](https://github.com/Basis-Theory/basistheory-android/commit/9ee959335788faf5a223c0dd7f9542bd7aa75171))


## [0.11.0](https://github.com/Basis-Theory/basistheory-android/compare/0.10.0...0.11.0) (2022-12-15)


### Features

* computing cvc mask based on card brand ([#20](https://github.com/Basis-Theory/basistheory-android/issues/20)) ([86fe06f](https://github.com/Basis-Theory/basistheory-android/commit/86fe06f57530cc99ce33b64dc290f6972f70a1e9))


## [0.10.0](https://github.com/Basis-Theory/basistheory-android/compare/0.9.0...0.10.0) (2022-12-14)


### Features

* Allows custom masks, transforms, and validators on TextElement ([#19](https://github.com/Basis-Theory/basistheory-android/issues/19)) ([ceaf62c](https://github.com/Basis-Theory/basistheory-android/commit/ceaf62c48249df94be7f4b3ec850c965d46af0b2))


## [0.9.0](https://github.com/Basis-Theory/basistheory-android/compare/0.8.0...0.9.0) (2022-12-13)


### Features

* Saves and restores instance state on elements ([#16](https://github.com/Basis-Theory/basistheory-android/issues/16)) ([783f153](https://github.com/Basis-Theory/basistheory-android/commit/783f153bcca190befbbd124856f6dd7aa9040245))


## [0.8.0](https://github.com/Basis-Theory/basistheory-android/compare/0.7.0...0.8.0) (2022-12-12)


### Features

* Adds card brand specific validation and masking ([#17](https://github.com/Basis-Theory/basistheory-android/issues/17)) ([cd87b8e](https://github.com/Basis-Theory/basistheory-android/commit/cd87b8e43bc5df4d2b5819475d46c0048c72ccd3))


## [0.7.0](https://github.com/Basis-Theory/basistheory-android/compare/0.6.0...0.7.0) (2022-12-09)


### Features

* add navigation to example app ([#13](https://github.com/Basis-Theory/basistheory-android/issues/13)) ([28b2320](https://github.com/Basis-Theory/basistheory-android/commit/28b23208d99c0f44b1d159dfdfc7a75c80dc4ce8))


## [0.6.0](https://github.com/Basis-Theory/basistheory-android/compare/0.5.0...0.6.0) (2022-12-09)


### Features

* Adds CardExpirationDateElement ([#15](https://github.com/Basis-Theory/basistheory-android/issues/15)) ([464d376](https://github.com/Basis-Theory/basistheory-android/commit/464d37633a5e50ed0bdd2d806cc413593babf818))


## [0.5.0](https://github.com/Basis-Theory/basistheory-android/compare/0.4.0...0.5.0) (2022-12-07)


### Features

* Adds CardVerificationCodeElement ([#14](https://github.com/Basis-Theory/basistheory-android/issues/14)) ([f90e028](https://github.com/Basis-Theory/basistheory-android/commit/f90e02820533dcc972db73b6f8415e87fe701bf7))


## [0.4.0](https://github.com/Basis-Theory/basistheory-android/compare/0.3.0...0.4.0) (2022-12-02)


### Features

* Adds CardNumberElement and validator property to TextElement ([#10](https://github.com/Basis-Theory/basistheory-android/issues/10)) ([968e782](https://github.com/Basis-Theory/basistheory-android/commit/968e78240c0179923514bc086d20f81b113cd0ca))


## [0.3.0](https://github.com/Basis-Theory/basistheory-android/compare/0.2.0...0.3.0) (2022-12-01)


### Features

* add masking ([#9](https://github.com/Basis-Theory/basistheory-android/issues/9)) ([b64dea2](https://github.com/Basis-Theory/basistheory-android/commit/b64dea2268499218f515b293d9210d3a9f4b9a64))


## [0.2.0](https://github.com/Basis-Theory/basistheory-android/compare/0.1.0...0.2.0) (2022-11-23)


### Features

* Adds transform property to TextElement ([#8](https://github.com/Basis-Theory/basistheory-android/issues/8)) ([9a2bcd3](https://github.com/Basis-Theory/basistheory-android/commit/9a2bcd38ebbb9b7c34b79ada45609b5981b3d359))


## [0.1.0](https://github.com/Basis-Theory/basistheory-android/compare/0.0.0...0.1.0) (2022-11-17)


