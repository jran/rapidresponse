$(function() {
 
    Parse.$ = jQuery;
 
    Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
    
    $('.signup-form').on('submit', function(e) {
      // Prevent Default Submit Event
      e.preventDefault();
   
      // Get data from the form and put them into variables
      var data = $(this).serializeArray(),
        firstname = data[0].value,
        lastname = data[1].value,
        email = data[2].value,
        password = data[3].value,
        password2 = data[4].value,
        role = "Page Operator";
        uphs = "@uphs.upenn.edu";
   
      //Ensure that passwords match, appropriately long, and uphs
      if (password != password2) {
        alert("Passwords do not match");
      } else if (password.length < 6) {
        alert("Password not long enough");
      } else if (email.indexOf(uphs) == -1)  {
        alert("Email must be a uphs email");
      } else {

        //Create user and save to parse
        var user = new Parse.User();     
        user.set("FirstName", email);
        user.set("LastName", email);
        user.set("username", email);
        user.set("password", password);
        user.set("email", email);
        user.set("Role", "Page Operator");
        
        user.signUp(null, {
              //Client has successfully logged in
              success: function(user) {
                alert("Please check your email for a verification email");
                window.location = "login.html";
              }
         });
      }
  });
});
