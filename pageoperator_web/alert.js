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
function submit(){
   console.log("submit works");
   location = document.getElementsByName('building');
   room = document.getElementsByName('room');
   emergencyType = document.getElementsByName('emergencyType').value;
   details = document.getElementsByName('description').value;
   phone = document.getElementsByName('phone').value;
   
   Parse.initialize("MEVkVnjwbter5JAP7mZIeg7747UA1QiBb7mOZ4Ch", "kc6tbhjMB2zRYtkicSxjhwQ8CeqKBIHceFkkGdzG");
   var query = new Parse.Query(Parse.Installation);
   Parse.Push.send({
      where: query,
      data: {
         alert: emergencyType + " in " + room + ". Details: "+ details +"\nContact: "+phone,
         emergency: emergencyType,
         room: room,
         details: details,
         contact: phone,
         sound: "cheering.caf",
         title: "Rapid Response!"
      }
   }, {
      success: function(){
         //push was successful
      },
      error: function(error){
         //Handle error
      }
   
   });
  

}