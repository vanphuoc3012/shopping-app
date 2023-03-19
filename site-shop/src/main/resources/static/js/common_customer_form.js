let dropDownCountry;
let dataListState;
let fieldState;

$(document).ready(function() {
	dropDownCountry = $("#country");
	dataListState = $("#listStates");
	fieldState = $("#state");
	
	dropDownCountry.on("change", function() {
		loadStatesForCountry();
		fieldState.val("").focus();
	});
});

function loadStatesForCountry() {
	let selectedCountry = $("#country option:selected");
	let countryId = selectedCountry.val();
	let url = contextPath + "settings/list_states_by_country/" + countryId;
	
	$.get(url, function(responseJSON) {
		dataListState.empty();
		
		$.each(responseJSON, function(index, state) {
			$("<option>").val(state.name).text(state.name).appendTo(dataListState);
		});
		
	}).fail(function() {
		alert('Failed to connect to the server!');
	});
}

function checkPasswordMatch(confirmPassword) {
	if (confirmPassword.value != $("#password").val()) {
		confirmPassword.setCustomValidity("Passwords do not match!");
	} else {
		confirmPassword.setCustomValidity("");
	}
}