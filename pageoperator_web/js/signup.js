$(function() {
 
    Parse.$ = jQuery;
 
    Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
    $('.signup-form').on('submit', function(e) {
      // Prevent Default Submit Event
      e.preventDefault();
   
      // Get data from the form and put them into variables
      var data = $(this).serializeArray(),
        email = data[0].value,
        password = data[1].value,
        password2 = data[2].value,
        role = "Page Operator";
   
      // Call Parse Signup function with those variables
      if (password != password2) {
        alert("Passwords do not match");
        window.location = "signup.html";
      }

      var user = new Parse.User();
      user.set("username", email);
      user.set("password", password);
      user.set("email", email);
      user.set("role", "Page Operator");
      
      user.signUp(null, {
            //Client has successfully logged in
            success: function(user) {
              alert("Please check your email for a verification email");
              window.location = "login.html";
            }
    });
  });
});
