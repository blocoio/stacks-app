
<p align="center">
<img src="images/circlesLogo.png" height="200" alt="Circles" />
</p>

<p align="center">
<a href="https://github.com/blocoio/stacks-circles-app/actions?query=workflow%3Alint"><img src="https://github.com/blocoio/stacks-circles-app/workflows/lint/badge.svg" alt="Lint Badge"></a>
<a href="https://github.com/blocoio/stacks-circles-app/actions?query=workflow%3Atest"><img src="https://github.com/blocoio/stacks-circles-app/workflows/test/badge.svg" alt="Testing Badge"></a>
<a href="https://opensource.org/licenses/MIT"><img src="https://img.shields.io/badge/license-MIT-purple.svg" alt="License: MIT"></a>
<a href="https://github.com/blocoio/stacks-circles-app/releases"><img src="https://img.shields.io/github/v/release/blocoio/stacks-circles-app.svg?style=flat" alt="Latest release"></a>
<a href="https://github.com/blocoio/stacks-circles-app/blob/main/CHANGELOG.md"><img src="https://img.shields.io/badge/Changelog-%20-yellow" alt="Changelog"></a>
</p>

<p align="center">
<a href='https://play.google.com/store/apps/details?id=io.bloco.circles'><img  height='55' src='images/google_play_badge.png'></a>
</p>

# BETA

Currently the app is in Beta, if you want to join our testing group take
a look at [Google Playstore
Testing](https://play.google.com/apps/testing/io.bloco.circles), or download and install our APK from the releases and use [our v0.1 PR for
feedback](https://github.com/blocoio/stacks-circles-app/pull/28)

# Stacks Circles Android App

The Stacks Circles App provides a native Android experience to the [Stacks ecosystem](https://www.stacks.co/). You can:

- Learn more about Stacks
- Create a Stacks account
- Manage your account identities
- Safely sign in into web and Android apps using your Stacks account

This app is open sourced. The project was made possible by the [Stacks Grants program](https://github.com/stacksgov/Stacks-Grants/issues/22) and is currently maintained by [Bloco](http://bloco.io/).

Join our [Beta Testing](https://play.google.com/apps/testing/io.bloco.circles).

## Design

The design follows both the Stacks and Material UI/UX guidelines and it is also
open source, for future improvements and feedback. You can see the design project in [figma](https://www.figma.com/file/RQy9bq4EX4Pz4bWP9r01cQ/%F0%9F%93%B1-Blockstack-Auth-Android-App?node-id=95%3A0).

# Screenshots

 <table style="width:80%">
  <tr>
    <td><img src="images/2.png" alt="Normal Flow pic2"></td>
    <td><img src="images/3.png" alt="Normal Flow pic3"></td>
    <td><img src="images/4.png" alt="Normal Flow pic4"></td>
  </tr>
  <tr>
    <td><img src="images/signup1.png" alt="New Account pic1"></td>
    <td><img src="images/signup2.png" alt="New Account pic2"></td>
    <td></td>
  </tr>
  <tr>
    <td><img src="images/login1.png" alt="Login pic1"></td>
    <td><img src="images/login2.png" alt="Login pic2"></td>
    <td><img src="images/login3.png" alt="Login pic2"></td>
  </tr>
  <tr>
    <td><img src="images/5.png" alt="Normal Flow pic5"></td>
    <td></td>
    <td></td>
  </tr>
</table> 

# Demo Video


<p align="center">
<img src="images/loginDemo.gif" height='500' alt="Login Video Demo">
<img src="images/newAccountDemo.gif" height='500' alt="New Account Video Demo">
</p>

# Usage

For authenticating other apps, this app intercepts the `app.blockstack.org` URL. This way, it is  compatible with Android apps using the [Blockstack SDK](https://github.com/blockstack/blockstack-android) and web applications using [stacks.js](https://github.com/blockstack/ux).

If an account is already configured, the user simply needs to pick an Identity (or create a new one) to complete the Authentication with other apps. If no account is configured the user can
also complete the Login/Signup experience within the app to complete the Authentication.

# Roadmap

The current [Milestones](https://github.com/blocoio/stacks-circles-app/milestones?state=open) and [projects](https://github.com/blocoio/stacks-circles-app/projects) can be found in this repo.

## Future Goals:

- Support legacy recovery keys
- Dark Theme Support

# **Notes**

This project was built using [Bloco.io Android Template](https://github.com/blocoio/android-template) and follows it's clean architecture pattern. We recommend you take a look at it and its [article](https://www.bloco.io/blog/2020/android-app-starter-update) if you want to contribute.
