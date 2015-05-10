window.onload=function(){
	event.preventDefault();
	//get the alert object id from url
	var alertid = window.location.search.split("?")[1];

	//initialize all roles to be hidden
	initializeRoles = document.getElementsByName('role');
	for(var i=0, n=initializeRoles.length;i<n;i++) {
		initializeRoles[i].style.display = 'none';
  	}

	//initialize Parse
	Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");

	//query the alert id to get full details from database
	var submitAlert = Parse.Object.extend("Alert");
	var query = new Parse.Query(submitAlert);
	console.log(alertid);
	query.get(alertid, {
  		success: function(object) {
			var emergencyType = object.get("EmergencyType");
			var roles = object.get("Roles");
			//show the roles that are called in this alert
			if(roles!=null){
				for(var j=0, n=roles.length;j<n;j++) {
					document.getElementById(roles[j]).style.display = 'block';
  				}
			}
			//populate information from the alert
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
	//refresh every 3 seconds to check whether anyone responded
	setInterval(function(){ checkResponse(query, alertid);}, 3000);
};

//helper function to notify the second tier after the first tier declined
function secondNotify(numb) {
	//Get building and alert information
	var alertid = window.location.search.split("?")[1];
	var submitAlert = Parse.Object.extend("Alert");
	var query = new Parse.Query(submitAlert);
	//get details about an alert to populate the push notification
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

//helper function that check whether anyone accepted or declined for each roles and show it on the block
function checkResponse(query, alertid){
	//query to get a list of accepted users
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
				//query to get the user's first name and last name and phone number for users who accepted
				acceptQuery.find({
  					success: function(userAccept) {
						for(var i=0, n=userAccept.length;i<n;i++) {
    							var role = document.getElementById(userAccept[i].get('Role'));
							role.innerHTML=userAccept[i].get("FirstName")+" "+userAccept[i].get("LastName")+ " ("+userAccept[i].id+") accepted! Number: "+userAccept[i].get("Phone")+"\n";
						}
						//query to get first name and last name for users who declined
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