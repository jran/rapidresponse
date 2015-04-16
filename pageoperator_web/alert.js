function toggle(source) {
  checkboxes = document.getElementsByName('building');
  for(var i=0, n=checkboxes.length;i<n;i++) {
    checkboxes[i].checked = source.checked;
  }
}
function roles(s){
   selection = s.value; 
   medical = document.getElementsByName('medicine');
   surgical = document.getElementsByName('surgical');
	pharmacist = document.getElementsByName('pharmacist');
	ccops = document.getElementsByName('CCOPS');
	respiratory = document.getElementsByName('respiratory');
   anesthesia = document.getElementsByName('anesthesia');
	coordinator = document.getElementsByName('coordinator');
   
   switch(selection){
       case 'medical':
         medical[0].checked = true;
			surgical[0].checked = false;
         pharmacist[0].checked = true;
         ccops[0].checked = true;
			respiratory[0].checked = true;
			anesthesia[0].checked = false;
		   coordinator[0].checked = true;
         break;
       case 'surgical':
         medical[0].checked = false;
			surgical[0].checked = true;
         pharmacist[0].checked = true;
         ccops[0].checked = true;
			respiratory[0].checked = true;
			anesthesia[0].checked = false;
		   coordinator[0].checked = true;
         break;
       case 'anesthesia':
         medical[0].checked = false;
			surgical[0].checked = false;
         pharmacist[0].checked = false;
         ccops[0].checked = false;
			respiratory[0].checked = true;
			anesthesia[0].checked = true;
		   coordinator[0].checked = false;
         break;
       case 'code':
         medical[0].checked = true;
			surgical[0].checked = true;
         pharmacist[0].checked = true;
         ccops[0].checked = true;
			respiratory[0].checked = true;
			anesthesia[0].checked = true;
		   coordinator[0].checked = true;
         break;
       case 'airway':
         medical[0].checked = false;
			surgical[0].checked = true;
         pharmacist[0].checked = false;
         ccops[0].checked = false;
			respiratory[0].checked = true;
			anesthesia[0].checked = true;
		   coordinator[0].checked = false;
         break;
   }
}

$(function() {
        Parse.$ = jQuery;

      Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
    }); 

    $('.form-alert').on('submit', function(e) {
        // Prevent Default Submit Event
        e.preventDefault();
     
        // Get data from the form and put them into variables
        var data = $(this).serializeArray(),
            location = data[0].value,
            description = data[1].value;
     
        // Call Parse Login function with those variables
        /*Parse.User.logIn(username, password, {
            // If the username and password matches
            success: function(user) {
                alert('Welcome!');
            },
            // If there is an error
            error: function(user, error) {
                alert('Failed to login. Please make sure your password and username are correct');
            }
        });*/
    });
