
//function to determine if a field is blank
function isBlank(inputField){
    if(inputField.type=="checkbox"){
	if(inputField.checked)
	    return false;
	return true;
    }
    if (inputField.value==""){
	return true;
    }
    return false;
}

//function to highlight an error through colour by adding css attributes to the div passed in
function makeRed(inputDiv){
	inputDiv.style.backgroundColor="red"
	inputDiv.parentNode.style.backgroundColor="red";
	
}
//remove all error styles from the div passed in
function makeClean(inputDiv){
	inputDiv.style.backgroundColor="white"
	inputDiv.parentNode.style.backgroundColor="#FFFFFF";
		
}

//the main function must occur after the page is loaded, hence being inside the wondow.onload event handler.
window.onload = function(){
    var mainForm = document.getElementById("mainForm");

    //all inputs with the class required are looped through 
    var requiredInputs = document.querySelectorAll(".required");
    for (var i=0; i < requiredInputs.length; i++){
		requiredInputs[i].onfocus = function(){
			this.style.fontWeight = "bold"
	    	this.style.backgroundColor = "green";
		}
		requiredInputs[i].onblur = function(){
	    	this.style.fontWeight = "normal";
	    	this.style.backgroundColor = "#FFFFFF";
		}
    }
	
    //on submitting the form, "empty" checks are performed on required inputs.
    mainForm.onsubmit = function(e){
	//var requiredInputs = document.querySelectorAll(".required");
	for (var i=0; i < requiredInputs.length; i++){
	    if( isBlank(requiredInputs[i]) ){
			e.preventDefault();
			makeRed(requiredInputs[i]);
	    }
	    else{
			makeClean(requiredInputs[i]);
	    }
	}
    }
}