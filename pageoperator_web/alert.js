window.onload=function(){
   document.getElementsByName('medicine')[0].checked=true;
	document.getElementsByName('pharmacist')[0].checked=true;
	document.getElementsByName('CCOPS')[0].checked=true;
	document.getElementsByName('respiratory')[0].checked=true;
	document.getElementsByName('coordinator')[0].checked=true;
  
   document.getElementById('form-alert').onsubmit=function() {
      var location = document.getElementsByName('building');
      var room = document.getElementById('room').value;
      var emergencyType = document.getElementById('emergencyType').value;
      var details = document.getElementById('description').value;
      var phone = document.getElementById('phone').value;
      Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
      var query = new Parse.Query(Parse.Installation);
      Parse.Push.send({
         where: query,
         data: {
            alert: emergencyType + " in "+room+". Details: "+details+"\n Contact:"+phone,
            emergencyType: emergencyType,
            room: room,
            details: details,
            phone: phone,
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