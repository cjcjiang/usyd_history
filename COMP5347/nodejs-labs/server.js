var express = require('express');
var path = require('path');
var bodyParser = require('body-parser');
var survey = require('./app/routes/survey.server.routes.js');
var session = require('express-session');

var app = express();

app.locals.products = ['iphone7', 'huaweip9', 'Pixel XL', 'Samsung S7'];
app.locals.surveyresults = {
  fp: [0, 0, 0, 0],
  mp: [0, 0, 0, 0]
};

app.locals.title_flag = 0;
app.locals.user_vote = 0;

app.set('views', path.join(__dirname, 'views'));
app.use(express.static(path.join(__dirname, 'public')));
app.use(bodyParser.json())
app.use(bodyParser.urlencoded())
app.use(session({secret: 'ssshhhh', cookie:{maxAge:600000}}));
app.use('/survey', survey)
app.listen(3000, function() {
  console.log('survey app listening on port 3000!')
})
