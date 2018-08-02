### Pattern matching extension Pre-SIP

This document presents the design and rationale for an extension to name-based extractors to bring their expressive power closer to case classes. A pattern
match such as `s match { case x0 @ Some(x1) => body }` binds `x0` to an alias of scrutinee `s` with a *more precise* type. We extend (name-based) extractors to
have the same capability.

We refer to the accompanying paper for the motivation and the basic ideas.

## Background: name-based extractors

We design an extension to name-based extractors; we first recall the definition
of the concept. A value `Extractor` is a name-based extractor on scrutinee type
`S` if `Extractor` has member `unapply(s: S): T`, to use to perform a match, and
`T` has members `isEmpty: Boolean` (to test if the match was successful) and
`get: U`, where `U` represents the "destructured" scrutinee, as we describe
while discussing pattern-matching compilation.

To understand pattern-matching compilation, we describe how to compile a
non-nested pattern match of the form `s match { case x0 @ Extractor(x1, ..., xn)
=> body; otherBranches }`, where `s` is a variable, `otherBranches` is a list of
zero or more pattern-matching branches, and `x0 @` is an *alias pattern* that
binds `x0` to the scrutinee value inside `body`.

We can reduce other extractor-based matches to this form:
we consider here only non-nested pattern matches, as nested ones can be
translated into multiple non-nested matches. Moreover, we assume pattern-matches
have been made exhaustive by adding any needed fallback cases that throw
`MatchError`; currently Scala cannot express exhaustive matches using
extractors, hence `otherBranches` should never be empty.
Finally, the alias pattern `x0 @` is optional in source programs; if missing, we
add such a pattern with a fresh variable x0`.

Our match is currently compiled to the following code, where `y0` and `z0` stand
for fresh variables, and comments bracket conditional sections of the output:
```scala
val y0 = Extractor.unapply(s)
if (!y0.isEmpty) {
  val x0 = s
  val z0 = y0.get
  // If n = 1:
  val x1 = z0
  //else, if n > 1:
  val x1 = z0._1
  ...
  val xn = z0._n
  body
}
// If nonBranches is a non-empty branch list, we handle the case where the pattern-match fails.
else {
  s match {
    otherBranches
  }
}
```

## Extension

Alias patterns often bind aliases of the scrutinee with more precise types; that
happens for instance when combining alias patterns with matches against case
classes, as in `case x0 @ Some(x1) =>`. To allow similar refinements for
extractor patterns, we propose two small extension:
1. We enable name-based extractors to specify a more precise type for `x0`.
2. This extension is useful also for matches that bind `n = 0` variables, that
   is, *nullary* matches, which currently cannot be expressed via name-based
   extractors but only through Boolean ones. Hence, we extend name-based
   extractors to the case where `n = 0`.

Specifically, we modify the requirement on the return type `T` of `Extractor.unapply`.
1. If method `T.refinedScrutinee: X` is defined,
   `x0` will be bound to `y0.refinedScrutinee`, with its return type. The return
   value of `refinedScrutinee` is expected to be the original scrutinee passed to
   `y0`, but with a potentially more precise type.
2. If method `T.get` is not defined, `Extractor` can be used for nullary matches.

So the translation of our example becomes:
```scala
val y0 = Extractor.unapply(s)
if (!y0.isEmpty) {
  val x0 = y0.refinedScrutinee // we expect this coincides with s.
  // If n > 0:
  val z0 = y0.get
  // If n = 1:
  val x1 = z0
  //else, if n > 1:
  val x1 = z0._1
  ...
  val xn = z0._n
  body
}
// If nonBranches is a non-empty branch list, we handle the case where the pattern-match fails.
else {
  s match {
    otherBranches
  }
}
```

Here is a sketch of an example extractor:

```scala
trait Peano {
  ...
  trait SuccExtractor {
    ...
    // Refined type of scrutinee (extension)
    def unapply(nat: Nat): SuccOpt { def refinedScrutinee: Succ & nat.type }
  }
  trait SuccOpt {
    def refinedScrutinee: Succ
    def isEmpty: Boolean // did the match fail?
    def get: Nat // get pred
  }
}
```


### Design choices

- Should the type of `s` be widened? Widening does not help information hiding
  here, but it might complicate type inference sadly. But if we decide to
  perform widening, there seems to be no way to disable it. The best option
  seems `case (x0 : s.type) @ Extractor(...) =>`, which doesn't look too
  compelling, and might also suggests that `x0` should be *tested* for reference
  equality against `s`.
- Can the user violate constraints? Yes, but that will still preserve soundness.
- There is still a difference with the typing of case class pattern matching.
  When compiling `s match { case x0 @ Some(x1) => body }`, the type of `x0` will
  be `S & Some[_]`, where `S` be the widened type of `s`. This is sound: the value
  `v` of `s` inhabits both `S` and `Some[_]` (because of the pattern match), hence
  inhabits their intersection. Should we use a similar typing discipline when
  dealing with `refinedScrutinee`? No, because it would be both unsound and not
  strictly necessary:
  - unsound, because it would require a cast, which would fail if
    `refinedScrutinee` doesn't return `x0`, something the compiler cannot enforce.
  - not strictly necessary, because the same result can be achieved by using a
    singleton type as shown above.

### A possible warning

When defining `unapply`, if the return type defines `refinedScrutinee`, we
should check that `x.unapply.refinedScrutinee`'s type is `<: x.type`. If that
check fails, we should emit a warning.
That warning should be silenceable by some annotation on `unapply`. It could be
`@SuppressWarnings("refinedScrutinee")`, but some thought is needed.
