## Crate Pull Requests

Changes to Crate repositories are made via Pull Requests (PRs).  We follow the forking strategy, any change to a
repository must be done through a PR.  This [gist](https://gist.github.com/Chaser324/ce0505fbed06b947d962) provides a
good overview of the forking model if you need assistance.

### Forking workflow
- Fork the repository you would like to contribute to
- Create a branch in your fork for your contribution
- Apply changes to your branch including test updates and documentation
- Create a PR from your branch to the target upstream branch
- PR will be reviewed/approved by Crate engineers
- Once all reviews and checks pass the PR will be merged

### Agree to the Developer Certificate of Origin
All contributors must agree to the Developer Certificate of Origin version 1.1.  To confirm that you agree to the 
DCO all commits must contain a Signed-off-by line with an email matching the email from the commit.  For reference, 
the text below is version 1.1 of the [Developer Certificate of Origin](https://developercertificate.org/)
```
Developer Certificate of Origin
Version 1.1

Copyright (C) 2004, 2006 The Linux Foundation and its contributors.
1 Letterman Drive
Suite D4700
San Francisco, CA, 94129

Everyone is permitted to copy and distribute verbatim copies of this
license document, but changing it is not allowed.


Developer's Certificate of Origin 1.1

By making a contribution to this project, I certify that:

(a) The contribution was created in whole or in part by me and I
    have the right to submit it under the open source license
    indicated in the file; or

(b) The contribution is based upon previous work that, to the best
    of my knowledge, is covered under an appropriate open source
    license and I have the right under that license to submit that
    work with modifications, whether created in whole or in part
    by me, under the same open source license (unless I am
    permitted to submit under a different license), as indicated
    in the file; or

(c) The contribution was provided directly to me by some other
    person who certified (a), (b) or (c) and I have not modified
    it.

(d) I understand and agree that this project and the contribution
    are public and that a record of the contribution (including all
    personal information I submit with it, including my sign-off) is
    maintained indefinitely and may be redistributed consistent with
    this project or the open source license(s) involved.
```

### Git setup
If not configured globally, please configure your commits to provide a username and email:
```
git config user.name '<first name> <last name>'
git config user.email '<email address>'
```

To signoff contributions add the `-s/--signoff` option when committing to your branch:
```
git commit -s
## or 
git commit --signoff
```

### PR Guidelines
- avoid including unrelated commits
- avoid large changes unless absolutely required
- rebase your branch before submitting
- include test cases for any source code changes
- include documentation for changes that affect application contracts

### Creating Pull Requests
When submitting PRs please provide a title that accurately describes the change.  If the PR fixes an issue be sure
to reference the issue number in the PR description.  Keywords and syntax formatting for issue linking can be found
in the GitHub [documentation](https://help.github.com/en/github/managing-your-work-on-github/linking-a-pull-request-to-an-issue#linking-a-pull-request-to-an-issue-using-a-keyword).

### Code Review
Code review is an important part of our process, we want to ensure that we keep code quality high with each
contribution.  After submitting your PR we will assign one or more reviewers.  To avoid a large backlog of stale PRs 
we have automation to close requests that have no activity after 2 weeks.  Please make sure to review feedback and 
engage with our reviewers to get your contribution merged.

Our repositories use the [Reviewable](https://docs.reviewable.io/) code review tool, please review the documentation 
to understand how the review system works.  We use [TravisCI](https://travis-ci.com/) for our automated builds and tests. 
Once changes have been approved and the automated build and tests pass a Crate engineer will merge your PR.