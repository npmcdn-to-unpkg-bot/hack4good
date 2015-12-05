var Header = React.createClass({
    render: function () {
        return (
            <nav className="light-blue lighten-1" role="navigation">
                <div className="nav-wrapper container"><a id="logo-container" href="#" className="brand-logo">Logo</a>
                    <ul className="right hide-on-med-and-down">
                        <li><a href="#">Navbar Link</a></li>
                    </ul>

                    <ul id="nav-mobile" className="side-nav">
                        <li><a href="#">Navbar Link</a></li>
                    </ul>
                    <a href="#" data-activates="nav-mobile" className="button-collapse"><i className="material-icons">menu</i></a>
                </div>
            </nav>
        )
    }
});

var LanguageFlag = React.createClass({
    getDefaultProps: function () {
        return {
            baseUrl: 'assets/flags/512/',
            country: 'Germany'
        }
    },

    getFullUrl: function () {
        return this.props.baseUrl + this.props.country + ".png"
    },

    render: function () {
        return <img src={this.getFullUrl()} width="100%"/>;
    }
});

var LanguageTable = React.createClass({
    getDefaultProps: function () {
        return {
            speaking: ['Germany', 'United-kingdom'],
            learning: ['Albania', 'Bosnian', 'Kosovo']
        };
    },

    render: function () {
        return (
            <div>
                <div className="row">
                    <div className="col s6">You speak</div>
                    <div className="col s6">You want to learn</div>
                </div>
                <div className="row">
                    <div className="col s6"><LanguageFlag country="Germany" /></div>
                    <div className="col s6"><LanguageFlag country="Albania" /></div>
                </div>
            </div>);
    }
});

var RoleChooser = React.createClass({
    getDefaultProps: function() {
        return {
            roles: {
                helper: 1,
                refugee: 0
            },
            role: null
        }
    },

    getInitialState: function () {
        return {
            role: this.props.role
        }
    },

    printRole: function () {
        if (this.state.role == this.props.roles.helper) {
            return 'You\'re a helper';
        } else if (this.state.role == this.props.roles.refugee) {
            return 'You\'re a refugee';
        } else {
            return "Nothing";
        }
    },

    setHelper: function () {
        console.log("helper");
        this.setState({role: this.props.helper});
    },

    setRefugee: function() {
        console.log("refugee");
        this.setState({role: this.props.refugee});
    },

    setLanguages: function (speaking, learning) {
        this.setState({
            learning: learning,
            speaking: speaking
        });
    },

    render: function() {
        return (
            <div>
                <h2>Are you a <a href="#" onClick={this.setHelper}>Helper</a> or <a href="#" onClick={this.setRefugee}> Refugee</a></h2>

                <LanguageTable />

                <button className=""></button>
            </div>
        );
    }
});

var DocumentView = React.createClass({
    getInitialState: function () {
        var stateData = {};
        stateData.documentData = this.props;
        stateData.styleHide = {
            visibility: 'hidden',
            height: '0px'
        };
        return stateData;
    },

    extendView: function () {
        console.log("Click on doc"+ this.state.title);
    },

    render: function() {
        return (
        <div className="card blue-grey darken-1" onClick={this.extendView()}>
            <div className="card-content white-text">
                <span className="card-title">{this.state.documentData.title}</span>
                <p>{this.state.documentData.url}</p>
                <div style={this.state.styleHide}>
                    <iframe src={this.state.documentData.url}></iframe>
                </div>
            </div>
            <div className="card-action">
                <a href="#">add tags</a>
                <a href="#" className="activator">more</a>
            </div>
            <div className="card-reveal">
                <span className="card-title grey-text text-darken-4">{this.state.documentData.title}<i className="material-icons right">close</i></span>
                <iframe src={this.state.documentData.url}></iframe>
            </div>
        </div>);
    }
});

var ContentTaggingOverview = React.createClass({

    getInitialState: function () {
        var documents = getDocuments();

        console.log(documents);

        return {
            documents: documents
        }
    },

    getDocumentsCards: function () {
        var cards = [], i = 0;

        this.state.documents.data.forEach(function (document) {
            cards[i++] = (<div className="row"><DocumentView title={document.title} url={document.url} tags={document.tags}/></div>);
        }, this);

        return cards;
    },

    render: function () {
        return <div>{this.getDocumentsCards()}</div>;
    }
});

var ChatMessage = React.createClass({

    getInitialState: function() {
        var stateData = { };

        stateData.owner     = this.props.owner;
        stateData.data      = this.props.data;
        stateData.date      = this.props.date;
        stateData.owner     = getUser(stateData.owner);

        return stateData;
    },

    render: function () {
        console.log(this.state);

        return (
        <li className="collection-item avatar">
            <img src={this.state.owner.avatarUrl} alt="" className="circle" />
            <span className="title">{this.state.owner.name}</span>
            <p>{this.state.data}</p>
        </li>);
    }
});

var ChatRoom = React.createClass({
    getDefaultProps: function() {
        return {
            chatId: 1
        };
    },

    getInitialState: function () {
        var messagesData = getSession(this.props.chatId);

        messagesData.messageValue = "";

        return messagesData;
    },

    getChatMessages: function () {
        var chatroomData = [];

        this.state.messages.forEach(function (message) {
            chatroomData.push(<ChatMessage owner={message.owner} data={message.data} date={message.date} />)
        }, this);

        return chatroomData;
    },

    createMessage: function (messageString) {
        return {
            owner: 1,
            data: messageString,
            date: 15
        }
    },

    sendMessage: function (event) {
        var value = $(ReactDOM.findDOMNode(this.refs.messageValue));
        if (value.val().length > 0) {
            var msg = this.createMessage(value.val());
            var allMessages = this.state.messages;
            allMessages.push(msg);
            this.setState({messages: allMessages});
            value.val("");
            console.log("TOOOOOOODOOOOOOOOOO Sending message !!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        }
    },

    render: function() {
        return (<div>
            <ul className="collection">{this.getChatMessages()}</ul>
            <div>
                <input type="text" ref="messageValue" onChange={this.handleInput}/>
                <button className="btn waves-effect waves-light red darken-3"
                        type="submit" name="action" onClick={this.sendMessage}>Submit
                    <i className="material-icons right">send</i>
                </button>
            </div>
        </div>);
    }
});

var Question = React.createClass({
    getDefaultProps: function() {
        return {
            id: 1
        }
    },

    getInitialState: function() {
        var stateData = { }, sessionInfo = getSession(this.props.id);

        stateData.owner     = sessionInfo.owner;
        stateData.data      = sessionInfo.data;
        stateData.date      = sessionInfo.date;
        stateData.owner     = getUser(stateData.owner);
        stateData.topic     = sessionInfo.topic;
        stateData.sessionId = sessionInfo.sessionId

        return stateData;
    },

    openChat: function() {
        Content.changeState({active: <ChatRoom chatId={this.state.sessionId} />});
    },

    render: function () {
        return (
            <li className="collection-item avatar" onClick={this.openChat}>
                <img src={this.state.owner.avatarUrl} alt="" className="circle" />
                <span className="title">{this.state.owner.name}</span>
                <p>{this.state.data}</p>
            </li>);
    }
});

var QuestionsOverview = React.createClass({
    render: function() {
        return <ul className="collection"><Question id="3234" /></ul>;
    }
});

var Content = React.createClass({
    getDefaultProps: function() {
        return {
            active: <QuestionsOverview chatId="1"/>
        }
    },

    getInitialState: function () {
        return {
            active: this.props.active
        }
    },

    changeState: function (domElement) {
        this.setState({active: domElement});
    },

    render: function() {
        return this.state.active;
    }
});

var Footer = React.createClass({
    render: function() {
        return (
            <footer className="page-footer orange">
                <div className="container">
                    <div className="row">
                    </div>
                </div>
            </footer>
        );
    }
});

var App = React.createClass({
    render: function () {
        $('.button-collapse').sideNav();
        return (
            <div>
                <Header/>
                <Content/>
                <Footer/>
            </div>)
    }
});

ReactDOM.render(<App/>, document.getElementById('app'));
