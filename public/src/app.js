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
    render: function () {
        return (
        <li class="collection-item avatar">
            <img src="http://materializecss.com/images/yuna.jpg" alt="" class="circle" />
            <span class="title">Title</span>
            <p>First Line Second Line</p>
            <a href="#!" class="secondary-content"><i class="material-icons">grade</i></a>
        </li>);
    }
});

var ChatRoom = React.createClass({
    render: function() {
        return (<ul></ul>);
    }
});

var Content = React.createClass({
    getDefaultProps: function() {
        return {
            active: <ChatMessage />
        }
    },

    render: function() {
        return this.props.active;
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
