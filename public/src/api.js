	var protocol = "http://";
	var host = "10.32.51.186";
	var port = "4000";
	var application = "rest";

	var apiUrl = protocol + host + ":" + port + "/" + application;

	var sessionUrl = apiUrl + "/" + "sessions";
	var documentsUrl = apiUrl + "/" + "documents";
	var userUrl = apiUrl + "/" + "users";
	
	function getSession(isMock, id, onSuccess, onError) {
		var sessionObj = {
			'topic' : 'the holy doge',
			'data' : 'How can I discuss gracefully about the holy doge?',
			'ownerId' : 2,
			'date' : 1449317640,
			'helperId' : 4,
			'messages' : [
				{
					'ownerId' : 4,
					'data' : 'hey there, Im here to help you',
					'date' : 1449317640 	
				},
				{
					'ownerId' : 2,
					'data' : 'cool stuff buddy, I really like you',
					'date' : 1449317640 	
				},
				{
					'ownerId' : 4,
					'data' : 'stuff and thanks',
					'date' : 1449317640 	
				},
			]
		};
		
		if(isMock) {
			onSuccess(sessionObj);
		}else{
			doRequest("GET", sessionUrl + "/" + id, undefined, "json", onSuccess, onError);
		}
	};
	
	function getSessions(isMock, language, onSuccess, onError) {
		var sessions = [{
			'topic' : 'toilette',
			'data' : 'Can someone tell me where the toilette is?',
			'owner' : 6,
			'date' : 1449317640,
			'sessionId' : 20
		},{
			'topic' : 'Body',
			'data' : 'Can anyone tell me whats happening to my body?',
			'owner' : 5,
			'date' : 1449317640,
			'sessionId' : 49
		},{
			'topic' : 'Stuffs',
			'data' : 'There is a lot of freaky stuff goin on, right?',
			'owner' : 2,
			'date' : 1449317640,
			'sessionId' : 79
		},{
			'topic' : 'Test Topic',
			'data' : 'I need to test this toic, who could join me with this?',
			'owner' : 9,
			'date' : 1449317640,
			'sessionId' : 75
		},{
			'topic' : 'the holy doge',
			'data' : 'How can I discuss gracefully about the holy doge?',
			'owner' : 1,
			'date' : 1449317640,
			'sessionId' : 90
		}];
		
		if(isMock) {
			onSuccess(sessions);
		}else {
			doRequest("GET", sessionUrl, "json", onSuccess, onError);
		}
	}
	
	function getUser(isMock, id, onSuccess, onError) {
		var padawan = {
			'avatarUrl' : 'http://s3.amazonaws.com/37assets/svn/765-default-avatar.png',
			'name' : 'Ron Romba',
			'role' : 'HELPER'
		};
		
		if (isMock) {
			onSuccess(padawan);
		}else {
			doRequest("GET", userUrl + "/" + id, onSuccess, onError);
		}

				
	};
	
	function createSession(isMock, language, ownerId, question, topic, onSuccess, onError) {
		var url = sessionUrl + "?lang=" + language + "&ownerId=" + ownerId;
		
		var postData = {
			'data' : question,
			'topic' : topic
		};

		if (isMock) {
			onSuccess(12);
		} else {
			doRequest("POST", sessionUrl , "json", postData, onSuccess, onError);
		}
	}

	function postMessage(isMock, sessionId, ownerId, message, onSuccess, onError) {
		var sessionObj = {
			'topic' : 'the holy doge',
			'data' : 'How can I discuss gracefully about the holy doge?',
			'owner' : 2,
			'date' : 1449317640,
			'helper' : 4,
			'messages' : [
				{
					'owner' : 4,
					'data' : 'hey there, Im here to help you',
					'date' : 1449317640 	
				},
				{
					'owner' : 2,
					'data' : 'cool stuff buddy, I really like you',
					'date' : 1449317640 	
				},
				{
					'owner' : 4,
					'data' : 'stuff and thanks',
					'date' : 1449317640 	
				},
			]
		};
		if (isMock) {
			onSuccess(sessionObj);
		}else {
			var data = {
				'message' : message
			};
			
			var url = sessionUrl + "/" + sessionId + "?ownerId=" + ownerId;
			doRequest("POST", url, data, "json", onSuccess, onError);
		}
	};
	
	function getDocuments(isMock, language, onSuccess, onError) {
		var documents = {
			'data' : [{
				'title' : "this is a title",
				'tags' : [{
					'lang' : 'de',
					'name' : 'language',
					'date' : 1449317640
				}],
				'url' : 'http://refugee-board.de/',
				'date' : 1449317640
			}, {
				'title' : "this is another title",
				'tags' : [{
					'lang' : 'en',
					'name' : 'language',
					'date' : 1449317640
				}],
				'url' : 'http://refugee-board.de/',
				'date' : 1449317640
			},{
				'title' : "this is a title",
				'tags' : [{
					'lang' : 'de',
					'name' : 'language',
					'date' : 1449317640
				}],
				'url' : 'http://refugee-board.de/',
				'date' : 1449317640
			}]
		};
		if (isMock) {
			onSuccess(documents);	
		}else {
			doRequest("GET", documentsUrl, undefined, "json", onSuccess, onError);
		}
		
	};
	
	function updateDocument(id, tags, onSuccess, onError) {
		var url = documentsUrl + "/" + id + "/" + "tags";
		doRequest("POST", url, tags, "json", onSuccess, onError);
	}
	
	function createDocument(title, tags, docUrl, fileType, onSuccess, onError) {
		var url = documentsUrl + "?title=" + title + "&url=" + encodeURIComponent(docUrl) + "&filetype=" + fileType;
		var tagString = "&tags=";
		
		for (var i=0; i < tags.length; i++) {
			tagString = tagString + tags[i];  
		}; 
		
		doRequest("POST", url, "json", onSuccess, onError);
	}
	
	function doRequest(requestMethod, requestUrl, postData, requestDataType, onSuccess, onError) {
		var request = $.ajax({
			url: requestUrl,
			method: requestMethod,
		});
		
		if (requestDataType !== undefined) {
			console.log("datatype is defined");
			request.dataType = requestDataType;
		}
		
		if (postData !== undefined) {
			console.log("data is defined");
			request.data = postData;
		} 

		request.done(onSuccess);
		
		request.fail(onError); 
		
		request.always(function() {
			console.log("completed request");
		});
	};
	
	function isRealFunction(f) {
		return typeof f !== 'undefined' && $.isFunction(f);
	}
