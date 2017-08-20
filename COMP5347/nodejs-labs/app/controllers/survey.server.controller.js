var express = require('express');

module.exports.showForm = function(req, res) {
  products = req.app.locals.products
  res.render('../app/views/survey.ejs', {
    products: products
  })
};
module.exports.showResult = function(req, res) {
  console.log(req.body);
  gender = req.body.gender;
  productidx = req.body.vote;
  products = req.app.locals.products;
  surveyresults = req.app.locals.surveyresults;
  title_flag = req.app.locals.title_flag;
  user_vote = req.app.locals.user_vote;
  sess = req.session;
  if('vote' in sess){
    title_flag = 1;
    user_vote = sess.vote;
    res.render('../app/views/surveyresult.ejs', {
      products: products,
      surveyresults: surveyresults,
      title_flag: title_flag,
      user_vote: user_vote
    });
  }else{
    sess.vote = productidx;
    if (gender == 0){
      surveyresults.mp[productidx]++;
    }else{
      surveyresults.fp[productidx]++;
    }
    title_flag = 0;
    user_vote = productidx;
    res.render('../app/views/surveyresult.ejs', {
      products: products,
      surveyresults: surveyresults,
      title_flag: title_flag,
      user_vote: user_vote
    });
  }
};
