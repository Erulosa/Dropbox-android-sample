# Dropbox-android-demo 
Sample Android application demonstrating step-up authentication for accessing Dropbox files using [knurld.io APIs](https://developer.knurld.io)

## Prerequisites
1. Signup to get your own developer credentials from http://developer.knurld.io/
2. Make sure you have a Dropbox account

##Knurld Getting Started Guide
http://developer.knurld.io/getting-started-guide-0

## Bootstrap

Before running the application, you will need to create an `Application Model` and a `Consumer` object manually in Knurld's API Service. We've provided some `curl` commands you can run from the command line to do this, or you can adapt the commands to a tool of your choice.

In order to create these you will need your _Developer-Id_ (this was issued to you in a email upon registration) and _OAuth_ Token. These are required for every request to Knurld's backend service.

### Get OAuth Token

You will need your `client_id` and `client_secret` in order to complete this request. In the below example please replace the `***CLIENT_xxx***` with it's respective token.

```
curl -X "POST" "https://api.knurld.io/oauth/client_credential/accesstoken?grant_type=client_credentials" \
    -H "Content-Type: application/x-www-form-urlencoded" \
    --data-urlencode "client_id=***CLIENT_ID***" \
    --data-urlencode "client_secret=***CLIENT_SECRET***"
```

You will get a response that should look something like:

```
{
  "issued_at" : "1456444574593",
  "application_name" : "4011528f-xxxx-xxxx-xxxx-9903434d2a69",
  "scope" : "",
  "status" : "approved",
  "api_product_list" : "[Some Product]",
  "expires_in" : "3599",
  "developer.email" : "example@test.com",
  "token_type" : "BearerToken",
  "client_id" : "***CLIENT_ID***",
  "access_token" : "k35jnLE8EdCwBsfCRM0HUNZ82CH5",
  "organization_name" : "knurld",
  "refresh_token_expires_in" : "0",
  "refresh_count" : "0"
}
```

You want the `access_token` value from this result.

### Create Application Model

First, we will create an application model. This is required for both `Enrollment` and `Verification` objects. In the example below replace the `***AUTH-TOKEN***` and `***DEVELOPER-ID***` values with the _OAuth_ token retrieved above and your _Developer-Id_.

```
curl -X "POST" "https://api.knurld.io/v1/app-models" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer ***AUTH-TOKEN***" \
-H "Developer-Id: Bearer: ***DEVELOPER-ID***" \
-d '{"vocabulary":["Octagon","Diamond","Germany","Toronto","Cylinder"],"verificationLength":"3"}'
```

You will get a response similar to the following:

```
{
  "href": "https://api.knurld.io/v1/app-models/a67a3f337823e2d56ec264f8c314e5ba"
}
```

You will want to take the `id` portion of the `href` attribute, it's the last path segment and open `app/src/main/java/com/knurld/dropboxdemo/Config.java`. Replace `APP_MODEL_ID` with this value. The demo application will now always use this Application Model.

### Create Consumer

Next, we will create a consumer for use within the DropBox demo application. This is the `Consumer` for which enrollments and verifications will be associated during usage of the application. To create the consumer issue the following `curl` command, remembering to replace any `***VARIABLE***` values with their respective pieces.

* `***GENDER***` must be one of either `M`, or `F`
* `***USERNAME***` and `***PASSWORD***` can be any random string, but cannot be empty.

```
curl -X "POST" "https://api.knurld.io/v1/consumers" \
-H "Content-Type: application/json" \
-H "Authorization: Bearer ***AUTH-TOKEN***" \
-H "Developer-Id: Bearer: ***DEVELOPER-ID***" \
-d '{"username":"***USERNAME***","gender":"***GENDER***","password":"***PASSWORD***"}'
```

You will want to take the `id` portion of the `href` attribute, it's the last path segment and open `app/src/main/java/com/knurld/dropboxdemo/Config.java`. Replace `CONSUMER_ID` with this value. The demo application will now always use this `Consumer`.

### API Credentials

You will want to edit `app/src/main/java/com/knurld/dropboxdemo/Config.java` to replace some more config options.

#### Knurld

* `DEVELOPER_ID` - This was the developer id issued to you, via email, when you registered.
* `CLIENT_ID` - This is your client id for OAuth authentication, can be obtained from Knurld's Developer Portal.
* `ClIENT_SECRET` - This is your client secret for OAuth authentication, can also be obtained from Knurld's Developer Portal.

#### DropBox

If you do not already have an existing DropBox application, you will need to visit [DropBox](http://dropbox.com/developers) and create an app. It is straight-forward and quick to complete; once you're finished you'll be left at your DropBox application's settings page. You will need the _App key_ and _App secret_ from that page.

* `APP_KEY` - This is your DropBox API Key, found on your DropBox application's settings page as _App key_.
* `APP_SECRET` - This is your DropBox API Secret, found on your DropBox application's settings page as _App secret_.


