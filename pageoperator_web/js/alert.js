window.onload=function(){
	document.getElementById('medicine').checked=true;
	document.getElementById('pharmacist').checked=true;
	document.getElementById('CCOPS').checked=true;
	document.getElementById('respiratory').checked=true;
	document.getElementById('coordinator').checked=true;
	document.getElementById('form-alert').onsubmit=function() {
		event.preventDefault();
      		var location = document.getElementsByName('building');
      		var buildings = "";
      		for (var i=0, n=location.length; i<n; i++){
	  		buildings = buildings + location[i].value +",";
      		}
		buildings = buildings.substring(0, buildings.length-1);
      		var room = document.getElementById('room').value;
      		var emergencyType = document.getElementById('emergencyType').value;
      		var details = document.getElementById('description').value;
      		var phone = document.getElementById('phone').value;
		var checkboxes = document.getElementsByName('role');
		var roles = [];
		for(var i=0, n=checkboxes.length;i<n;i++) {
   	 		if(checkboxes[i].checked == true){
				roles.push(checkboxes[i].id);
			}
  		}

      		Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
      		var NewAlert = Parse.Object.extend("Alert");
      		var newAlert = new NewAlert();
      		newAlert.set("Description", details);
      		newAlert.set("Location", room);
      		newAlert.set("Building", buildings);
      		newAlert.set("EmergencyType", emergencyType);
      		newAlert.set("Phone", phone);
		newAlert.set("Roles", roles);
      		newAlert.save(null, {
	  		success: function(al) {
	      		console.log(al.id+" saved successfully");
	      		// The object was saved successfully.
	      		// send push notification
			var userQuery = new Parse.Query(Parse.User);
			userQuery.equalTo('Team', emergencyType);
			userQuery.equalTo('Building', buildings);

	      		var pushQuery = new Parse.Query(Parse.Installation);
			pushQuery.matchesQuery('user', userQuery);
	      		Parse.Push.send({
		  		where: pushQuery,
		  		data: {
		      			alert: emergencyType + " in "+room+". Details: "+details+"\nContact:"+phone,
		      			ObjectID: al.id,
		      			sound: "loudalert.wav"
		  		}
	      		}, {
		  		success: function(){
		      			console.log("Push was successful");
					alert("Alert Sent. Monitor Responses?");
					window.location = './afteralert.html?'+al.id;
		  		},
		  		error: function(error){
		      			console.log("Error: "+error.value);
		  		}
	      		});	          
	  	},
	  		error: function(al, error) {
	      			console.log(error.message);
	      			// The save failed.
	      			// error is a Parse.Error with an error code and message.
	  		}
      		});
   }

}


function toggle(source) {
	checkboxes = document.getElementsByName('building');
  	for(var i=0, n=checkboxes.length;i<n;i++) {
   	 	checkboxes[i].checked = source.checked;
  	}
}
function roles(s){
	selection = s.value; 
	medical = document.getElementById('medicine');
	surgical = document.getElementById('surgical');
	pharmacist = document.getElementById('pharmacist');
	ob = document.getElementById('ob');
	ccops = document.getElementById('CCOPS');
	respiratory = document.getElementById('respiratory');
	anesthesia = document.getElementById('anesthesia');
	coordinator = document.getElementById('coordinator');
   	switch(selection){
       		case 'Medical Rapid Response':
         		medical.checked = true;
			surgical.checked = false;
			ob.checked = false;
         		pharmacist.checked = true;
         		ccops.checked = true;
			respiratory.checked = true;
			anesthesia.checked = false;
		   	coordinator.checked = true;
         		break;
       		case 'Surgical Rapid Response':
         		medical.checked = false;
			surgical.checked = true;
         		pharmacist.checked = true;
         		ob.checked = false;
			ccops.checked = true;
			respiratory.checked = true;
			anesthesia.checked = false;
		   	coordinator.checked = true;
         		break;
		case 'OB Emergency':
         		medical.checked = false;
			surgical.checked = false;
			ob.checked = true;
         		pharmacist.checked = true;
         		ccops.checked = false;
			respiratory.checked = false;
			anesthesia.checked = false;
		   	coordinator.checked = true;
         		break;
       		case 'Anesthesia Stat':
         		medical.checked = false;
			surgical.checked = false;
         		ob.checked = false;
			pharmacist.checked = false;
         		ccops.checked = false;
			respiratory.checked = true;
			anesthesia.checked = true;
		   	coordinator.checked = true;
         		break;
       		case 'Code Call':
         		medical.checked = true;
			surgical.checked = true;
         		ob.checked = false;
			pharmacist.checked = true;
         		ccops.checked = true;
			respiratory.checked = true;
			anesthesia.checked = true;
		   	coordinator.checked = true;
         		break;
       		case 'Airway Emergency':
         		medical.checked = false;
			surgical.checked = true;
         		ob.checked = false;
			pharmacist.checked = false;
         		ccops.checked = false;
			respiratory.checked = true;
			anesthesia.checked = true;
		   	coordinator.checked = true;
         		break;
   }
}