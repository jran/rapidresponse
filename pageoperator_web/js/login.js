
$(function() {
 
    Parse.$ = jQuery;
 
    Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
    $('.login-form').on('submit', function(e) {
      // Prevent Default Submit Event
      e.preventDefault();
   
      // Get data from the form and put them into variables
      var data = $(this).serializeArray(),
          username = data[0].value,
          password = data[1].value;
   
      // Call Parse Login function with those variables
      Parse.User.logIn(username, password, {
          //Client has successfully logged in
          success: function(user) {
            window.location = "home.html";
          },
          //Incorrect login credentials
          error: function(user, error) {
              console.log(error);
              //If failed stay on login page, tell user login credentials incorrect
              window.location = "createalert.html";
          }
    });
  });
});
