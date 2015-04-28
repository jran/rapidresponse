window.onload=function(){
	event.preventDefault();
	var alertid = window.location.search.split("?")[1];

	Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
	var submitAlert = Parse.Object.extend("Alert");
	var query = new Parse.Query(submitAlert);
	console.log(alertid);
	query.get(alertid, {
  		success: function(object) {
			var emergencyType = object.get("EmergencyType");
			
			var typeInfo = document.getElementById('emergencytype');
			typeInfo.innerHTML = emergencyType;
			var detailInfo= document.getElementById('description');
			detailInfo.innerHTML= object.get("Description");
			var roomInfo = document.getElementById('room');
			roomInfo.innerHTML= object.get("Location");
			var buildingInfo = document.getElementById('building');
			buildingInfo.innerHTML= object.get("Building");
			var phoneInfo = document.getElementById('phone');
			phoneInfo.innerHTML=object.get("Phone");						

  		},
  		error: function(object, error) {
    			alert("Error: " + error.code + " " + error.message);
  		}
	});
	checkResponse(query, alertid);
}

function checkResponse(query, alertid){
	setInterval(function(){ 
		query.get(alertid, {
  			success: function(object) {
				var accepted = object.get("Accepted");
				var declined = object.get("Declined");
				var accept = document.getElementById('accepted');
				accept.innerHTML=accepted;
		  		},
  			error: function(object, error) {
    				alert("Error: " + error.code + " " + error.message);
  		}
		});
	}, 3000);
}

/*
	document.getElementById('form-alert').onsubmit=function() {
		event.preventDefault();
      		var location = document.getElementsByName('building');
      		var buildings = "";
      		for (var i=0, n=location.length; i<n; i++){
	  		buildings = buildings + location[i].value +",";
      		}
      		var room = document.getElementById('room').value;
      		var emergencyType = document.getElementById('emergencyType').value;
      		var details = document.getElementById('description').value;
      		var phone = document.getElementById('phone').value;
      		Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
      		var NewAlert = Parse.Object.extend("Alert");
      		var newAlert = new NewAlert();
      		newAlert.set("Description", details);
      		newAlert.set("Location", room);
      		newAlert.set("Building", buildings);
      		newAlert.set("EmergencyType", emergencyType);
      		newAlert.set("Phone", phone);
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
		      			sound: "cheering.caf"
		  		}
	      		}, {
		  		success: function(){
		      			console.log("Push was successful");
		  		},
		  		error: function(error){
		      			console.log("Error: "+error.value);
		  		}
	      		});
	      		alert("Form Submitted. Send a new one?");	      
			return false;	      
	  	},
	  		error: function(al, error) {
	      			console.log(error.message);
	      			// The save failed.
	      			// error is a Parse.Error with an error code and message.
	  		}
      		});
      
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
       		case 'Medical Rapid Response':
         		medical[0].checked = true;
			surgical[0].checked = false;
         		pharmacist[0].checked = true;
         		ccops[0].checked = true;
			respiratory[0].checked = true;
			anesthesia[0].checked = false;
		   	coordinator[0].checked = true;
         		break;
       		case 'Surgical Rapid Response':
         		medical[0].checked = false;
			surgical[0].checked = true;
         		pharmacist[0].checked = true;
         		ccops[0].checked = true;
			respiratory[0].checked = true;
			anesthesia[0].checked = false;
		   	coordinator[0].checked = true;
         		break;
       		case 'Anesthesia Stat':
         		medical[0].checked = false;
			surgical[0].checked = false;
         		pharmacist[0].checked = false;
         		ccops[0].checked = false;
			respiratory[0].checked = true;
			anesthesia[0].checked = true;
		   	coordinator[0].checked = false;
         		break;
       		case 'Code Call':
         		medical[0].checked = true;
			surgical[0].checked = true;
         		pharmacist[0].checked = true;
         		ccops[0].checked = true;
			respiratory[0].checked = true;
			anesthesia[0].checked = true;
		   	coordinator[0].checked = true;
         		break;
       		case 'Airway Emergency':
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
*/