package com.hm.routes

import spray.http.MediaTypes.`text/html`
import spray.routing.HttpService


/**
  * Created by swathi on 24/2/17.
  */
trait Routes extends HttpService with AuthenticationHandler
  with CountHandler {


  val route =

    path("") {


      get {
        respondWithMediaType(`text/html`) {
          // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Enter the product line or customer number</h1>
              </body>
            </html>
          }
        }
      }
    }~ path("countProductLine") {
      countByProductLine
    }~ path("countCustomer") {
      countByCustomer
    }~path("login")
    {
      login
    }~path("logout")
    {
      logout
    }~path("signup")
    {
      signup

    }

}
