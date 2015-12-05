jQuery(document).ready(function() {
	console.log(getSession(4));
	console.log(getUser(2));

	var protocol = "http://";
	var host = "10.32.51.186";
	var port = "4000";
	var application = "test";

	var apiUrl = protocol + host + ":" + port + "/" + application;

	var sessionUrl = apiUrl + "/" + "sessions";
	var documentsUrl = apiUrl + "/" + "documents";
	var documentsTagsUrl = documentsUrl + "/" + "tags";
	
	doRequest("GET", "http://www.google.de", { 'data' : 'test' }, undefined, function() {
		console.log("on success");
	}, function() {
		console.log("on fail");
	});
	
	// lang, ownerid, question, topic, onSucces, onError
	createSession("de", "2", "Could someone pls tell me whats happening to my body?", "My Body", function() {
		console.log("on success");
	}, function() {
		console.log("on error");
	});
	
});
