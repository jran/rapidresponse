$(window).on("html_loaded", function(e) {
    $(".submit_button").click(function(e) {
        
        // 1. Add your Parse keys.
        Parse.initialize("application_id","javascript_key");
        
        // 2. Create a new Parse.User.
        var user = new Parse.User();

        // 3. Set a username.
        user.setUsername();

        // 4. Set a password.
        user.setPassword();
        
        // 5. Sign them up!
        user.signUp(null, {
            success: function(user) {
                console.log("User signed up!");
            }
        });
    });
});