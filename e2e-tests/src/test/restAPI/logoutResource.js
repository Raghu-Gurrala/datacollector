var frisby = require('frisby');

frisby.create('Login to StreamSets Data Collector')
  .get(browser.baseUrl + 'login?j_username=admin&j_password=admin')
  .expectStatus(200)
  .expectHeader('Content-Type', 'text/html')
  .after(function(body, res) {
    var cookie = res.headers['set-cookie'];


    /**
     * GET rest/v1/admin/logout
     */
    frisby.create('Should be able to logout SDC')
      .post(browser.baseUrl + 'rest/v1/logout', {
        headers:  {
          "Cookie": cookie
        }
      })
      .expectStatus(303)
      .after(function(body, res) {
        var cookie = res.headers['set-cookie'];
        frisby.create('Trying to access build info after logout should redirect to login page.')
          .get(browser.baseUrl + 'rest/v1/admin/threadsDump', {
            headers:  {
              "Content-Type": "application/json",
              "Accept": "application/json",
              "Cookie": cookie
            }
          })
          .expectStatus(200)
          .expectHeaderContains('content-type', 'text/html')
          .toss();
      })
      .toss();
  })
  .toss();
