//the main function must occur after the page is loaded, hence being inside the wondow.onload event handler.
window.onload = function () {
	var mainForm = document.getElementById("mainForm");

	//all inputs with the class required are looped through
	var requiredInputs = document.querySelectorAll(".required");
	for (var i = 0; i < requiredInputs.length; i++) {
		requiredInputs[i].onfocus = function () {
			this.style.fontWeight = "bold";
			this.style.backgroundColor = "green";
		}
		requiredInputs[i].onblur = function () {
			//TODO,
			//highlight an error if no value
			if (isBlank(this)) {
				makeYellow(this);
			} else {
				// this.style.fontWeight = "normal";
				// this.style.backgroundColor = "#FFFFFF";
				makeClean(this);
				this.style.fontWeight = "normal";
			}

		}
	}

	//on submitting the form, "empty" checks are performed on required inputs.
	mainForm.onsubmit = function (e) {
		//TODO, perform empty checks
		for (var i = 0; i < requiredInputs.length; i++) {
			if (isBlank(requiredInputs[i])) {
				e.preventDefault();
				makeRed(requiredInputs[i]);
			} else {
				makeClean(requiredInputs[i]);
			}

			alert('You have submitted the form');
		}
	}
}

function isBlank(inputField) {
	if (inputField.type == "checkbox") {
		if (inputField.checked) {
			return false;
		}
		return true;
	}
	if (inputField.value == "") {
		return true;
	}
	return false;
}

function makeRed(inputDiv) {
	inputDiv.style.backgroundColor = "red"
		inputDiv.parentNode.style.backgroundColor = "red";
}

//remove all error styles from the div passed in
function makeClean(inputDiv) {
	inputDiv.style.backgroundColor = "white"
		inputDiv.parentNode.style.backgroundColor = "#FFFFFF";
}

function makeYellow(inputDiv) {
	inputDiv.style.backgroundColor = "yellow"
		inputDiv.parentNode.style.backgroundColor = "yellow";
}
