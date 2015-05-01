window.onload  = function() {
    Parse.$ = jQuery;
 
    Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");

    var currentUser = Parse.User.current();
  if (currentUser) {
    // do stuff with the user
    alert(Parse.User.current);
  } else {
     // show the signup or login page
     window.location = "login.html";
   }
};
