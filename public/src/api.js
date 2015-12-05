	function getSession(id) {
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
		return sessionObj;
	};
	
	function getSessions() {
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
	}
	
	function getHelper(id) {
		var helper = {
			'avatarUrl' : 'http://s3.amazonaws.com/37assets/svn/765-default-avatar.png',
			'name' : 'Ron Romba',
			'role' : 'HELPER'
		};
		return helper;
	};
	
	function getPadawan(id) {
		var padawan = {
			'avatarUrl' : 'http://s3.amazonaws.com/37assets/svn/765-default-avatar.png',
			'name' : 'Ron Romba',
			'role' : 'HELPER'
		};
		return padawan;
	};

	function postMessage(sessionId, message) {
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
		return sessionId;
	};
	
	function getDocuments(language) {
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
		return documents;
	};
	
	function doRequest(requestMethod, requestUrl, requestDataType, onSuccess, onError) {
		doRequest(requestMethod, requestUrl, undefined, requestDataType, onSuccess, onError);
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
