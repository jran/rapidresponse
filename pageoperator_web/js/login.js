$(function() {
 
    Parse.$ = jQuery;
 
    Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
    $('.login-form').on('submit', function(e) {
      // Prevent Default Submit Event
      e.preventDefault();
   
      // Get data from the form and put them into variables
      var data = $(this).serializeArray(),
          email = data[0].value,
          password = data[1].value;
   
      // Call Parse Login function with those variables
      Parse.User.logIn(email, password, {
          //Client has successfully logged in: redirect to create alert page
          success: function(user) {
            window.location = "createalert.html";
          },
          //Incorrect login credentials: Display alert
          error: function(user, error) {
              console.log(error);
              document.getElementById("errormess").innerHTML = "Username or password incorrect. Please try again";
              window.location = "login.html";
          }
    });
  });
});
