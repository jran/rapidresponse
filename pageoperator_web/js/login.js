
$(function() {
 
    Parse.$ = jQuery;
 
    Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
}); 

    $('.login-form').on('submit', function(e) {
      // Prevent Default Submit Event
      e.preventDefault();
   
      // Get data from the form and put them into variables
      var data = $(this).serializeArray(),
          username = data[0].value,
          password = data[1].value;
   
      // Call Parse Login function with those variables
      Parse.User.logIn(username, password, {
          // If the username and password matches
          success: function(user) {
              alert('Welcome!');
          },
          // If there is an error
          error: function(user, error) {
              console.log(error);
          }
    });

});
