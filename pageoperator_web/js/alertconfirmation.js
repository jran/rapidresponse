window.onload=function(){
	event.preventDefault();
	var alertid = window.location.search.split("?")[1];
	initializeRoles = document.getElementsByName('role');
	for(var i=0, n=initializeRoles.length;i<n;i++) {
		initializeRoles[i].style.display = 'none';
  	}

	Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
	var submitAlert = Parse.Object.extend("Alert");
	var query = new Parse.Query(submitAlert);
	console.log(alertid);
	query.get(alertid, {
  		success: function(object) {
			var emergencyType = object.get("EmergencyType");
			var roles = object.get("Roles");
			if(roles!=null){
				for(var j=0, n=roles.length;j<n;j++) {
					document.getElementById(roles[j]).style.display = 'block';
  				}
			}
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
	setInterval(function(){ checkResponse(query, alertid);}, 3000);
};
function secondNotify(numb) {
	//Get building and alert information
	var alertid = window.location.search.split("?")[1];
	var submitAlert = Parse.Object.extend("Alert");
	var query = new Parse.Query(submitAlert);

	query.get(alertid, {
  		success: function(object) {
			var emergencyType = object.get("EmergencyType");
			var detailInfo = object.get("Description");
			var roomInfo =  object.get("Location");
			var buildingInfo = object.get("Building");
			var phoneInfo = object.get("Phone");

			//Get building(s) so that alert is not sent to same people
			var buildings = object.get("Building");
			alert(buildings);
			var buildingsAlerted = buildings.split(",");
			var allBuildings = ["Founders", "Perelemen Center", "Rhoads", "Silverstein"];
			var buildingForQuery = [];
			for(var i = 0; i < allBuildings.length; i++) {
				var alerted = false;
				for(var k = 0; k < buildingsAlerted.length; k++) {
					if (allBuildings[i] == buildingsAlerted[k])
						alerted = true;
				}

				if (!alerted) {
					buildingForQuery.push(allBuildings[i]);
				}
			}
			alert(buildingForQuery);
			//Get Role
			var role = "";
				if (numb == 1) {
					role = "Medicine Resident";
				} else if (numb == 2) {
					role = "Surgical Resident";
				} else if (numb == 3) {
					role = "OB Resident";
				} else if (numb == 4) {
					role = "Pharmacist";
				} else if (numb == 5) {
					role = "CCOPS";
				} else if (numb == 6) {
					role = "Respiratory Therapist";
				} else if (numb == 7) {
					role = "Coordinator";
				} else {
					role = "Anesthesia";
				}	

				//Execute Query
				var userQuery = new Parse.Query(Parse.User);
				userQuery.equalTo('Role', role);
				userQuery.containedIn('Building', buildingForQuery);

				var pushQuery = new Parse.Query(Parse.Installation);
				pushQuery.exists("user"); // filter out installations without users
			 	pushQuery.include('user'); // expand the user pointer
				pushQuery.matchesQuery('user', userQuery);
				Parse.Push.send({
					where: pushQuery,
					data: {
						alert: emergencyType + " in "+ roomInfo+". Details: "+ detailInfo +"\nContact:"+phoneInfo,
						ObjectID: alertid,
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
  		error: function(object, error) {
    			alert("Error: " + error.code + " " + error.message);
  		}
  	});
}
function checkResponse(query, alertid){
	query.get(alertid, {
  			success: function(object) {
				var acceptList = object.get("Accepted");
				if(acceptList!=null){
					var accepted = acceptList.split(",");
				}
				var declineList = object.get("Declined");
				if(declineList!=null){
					var declined= declineList.split(",");
				}
				var acceptQuery = new Parse.Query(Parse.User);
				acceptQuery.containedIn("objectId", accepted);
				acceptQuery.find({
  					success: function(userAccept) {
						for(var i=0, n=userAccept.length;i<n;i++) {
    							var role = document.getElementById(userAccept[i].get('Role'));
							role.innerHTML=userAccept[i].get("FirstName")+" "+userAccept[i].get("LastName")+ " ("+userAccept[i].id+") accepted! Number: "+userAccept[i].get("Phone")+"\n";
						}
						var declineQuery = new Parse.Query(Parse.User);
						declineQuery.containedIn("objectId", declined);
						declineQuery.find({
  							success: function(userDecline) {
								for(var i=0, n=userDecline.length;i<n;i++) {
    									var role = document.getElementById(userDecline[i].get('Role'));
									role.innerHTML=userDecline[i].get("FirstName")+" "+userDecline[i].get("LastName")+ " ("+userDecline[i].id+") declined. Notify others?"+"\n";
								}


  							}
						});

  					}
				});

			},
  			error: function(object, error) {
    				alert("Error: " + error.code + " " + error.message);
  			}
	});
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

*/