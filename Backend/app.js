const express = require('express')
const request = require('request')
const url = require('url')
const app = express();
const cors = require('cors')
const googleTrends = require('google-trends-api');

app.use(function(req, res, next) {
    res.header("Access-Control-Allow-Origin", "*");
    res.header("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
    next();
  });
app.use(cors())
const PORT = process.env.PORT || 5000;
app.get('/', (req, res) => {
  res.send('Hello World!')
});

   app.get('/guardian', (req, res) => {
    var data = [];
    var url = "https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=88c25341-0bf1-4241-a346-90a93f2090af";
    request(url, { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    console.log(body.response.results);
    let results = body.response.results;
    let count = 0;
    for(let i = 0; i < results.length; i++){
        if(count === 10)
            break;
        let article = {};
        let img = results[i].fields.thumbnail;
        if(results[i].sectionName != "" && results[i].webTitle != "" && results[i].webPublicationDate != "" && img != ""){
            article["title"] = results[i].webTitle;
            article["section"] = results[i].sectionName;
            article["published_date"] = results[i].webPublicationDate;
            if(img === undefined || img === "undefined" || img === ""){
                article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
            }
            else{
                article["image"] = img;
            }
            article["id"] = results[i].id;
            article["url"] = results[i].webUrl;
            data[count] = article;
            count++;
        }
    }
    res.send(data);
    });
  });

  app.get('/trends', (req, res) => {
  	let query = url.parse(req.url,true).query;
  	googleTrends.interestOverTime({
	keyword: query.query,
	startTime: new Date(2019, 5, 1)
	}).then((response) => {
		let values = [];
  		let obj = JSON.parse(response);
  		let defa = obj.default;
  		let timelineData = defa.timelineData;
  		console.log(timelineData);
  		for(let i = 0; i < timelineData.length; i++){
  			values[i] = timelineData[i].value[0];
  		}
  		let result = {};
  		result["values"] = values;
  		res.send(result);
	})
	.catch((err) => {
  	console.log('got the error', err);
  	console.log('error message', err.message);
  	console.log('request body',  err.requestBody);
	})
});

  app.get('/guardianWorld', (req, res) => {
    var data = [];
    request("https://content.guardianapis.com/world?api-key=88c25341-0bf1-4241-a346-90a93f2090af&show-blocks=all&page-size=20", { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    let results = body.response.results;
    let count = 0;
    for(let i = 0; i < results.length; i++){
        if(count === 10)
            break;
        if(results[i] === undefined || results[i] === "undefined" || results[i] === null){
            continue;
        }
        let article = {};
        let a = results[i].blocks;
        if(a === null || a === "undefined" || a === undefined)
            continue;
        let b = a.main;
        if(b === null || b === "undefined" || b === undefined)
            continue;
        let c = b.elements[0];
        if(c === null || c === "undefined" || c === undefined)
            continue;
        let urls = c.assets;
        if(urls.length == 0)
            continue;
        let img = urls[urls.length-1].file;
        if(results[i].webUrl != "" && results[i].id != "" && results[i].sectionId != "" && results[i].webTitle != "" && results[i].webPublicationDate != "" && results[i].webUrl != undefined && results[i].id != undefined && results[i].sectionId != undefined && results[i].webTitle != undefined && results[i].webPublicationDate != undefined){
            article["title"] = results[i].webTitle;
            article["section"] = results[i].sectionName;
            article["published_date"] = results[i].webPublicationDate
            if(img === undefined || img === "undefined" || img === ""){
                article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
            }
            else{
                article["image"] = img;
            }
            article["id"] = results[i].id;
            article["url"] = results[i].webUrl;
            data[count] = article;
            count++;
        }
    }
    res.send(data);
    });
  });

  app.get('/guardianPolitics', (req, res) => {
    var data = [];
    request("https://content.guardianapis.com/politics?api-key=88c25341-0bf1-4241-a346-90a93f2090af&show-blocks=all&page-size=20", { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    let results = body.response.results;
    let count = 0;
    for(let i = 0; i < results.length; i++){
        if(count === 10)
            break;
        if(results[i] === undefined || results[i] === "undefined" || results[i] === null){
            continue;
        }

        let article = {};
        let a = results[i].blocks;
        if(a === null || a === "undefined" || a === undefined)
            continue;
        let b = a.main;
        if(b === null || b === "undefined" || b === undefined)
            continue;
        let c = b.elements[0];
        if(c === null || c === "undefined" || c === undefined)
            continue;
        let urls = c.assets;
        if(urls.length == 0)
            continue;
        let img = urls[urls.length-1].file;
        if(results[i].webUrl != "" && results[i].id != "" && results[i].sectionId != "" && results[i].webTitle != "" && results[i].webPublicationDate != "" && results[i].webUrl != undefined && results[i].id != undefined && results[i].sectionId != undefined && results[i].webTitle != undefined && results[i].webPublicationDate != undefined){
            article["title"] = results[i].webTitle;
            article["section"] = results[i].sectionName;
            article["published_date"] = results[i].webPublicationDate
            let k;
            if(img === undefined || img === "undefined" || img === ""){
                article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
            }
            else{
                article["image"] = img;
            }
            article["id"] = results[i].id;
            article["url"] = results[i].webUrl;
            data[count] = article;
            count++;
        }
    }
    res.send(data);
    });
  });

  app.get('/guardianBusiness', (req, res) => {
    var data = [];
    request("https://content.guardianapis.com/business?api-key=88c25341-0bf1-4241-a346-90a93f2090af&show-blocks=all&page-size=20", { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    let results = body.response.results;
    let count = 0;
    for(let i = 0; i < results.length; i++){
        if(count === 10)
            break;
        if(results[i] === undefined || results[i] === "undefined" || results[i] === null){
            continue;
        }

        let article = {};
        let a = results[i].blocks;
        if(a === null || a === "undefined" || a === undefined)
            continue;
        let b = a.main;
        if(b === null || b === "undefined" || b === undefined)
            continue;
        let c = b.elements[0];
        if(c === null || c === "undefined" || c === undefined)
            continue;
        let urls = c.assets;
        if(urls.length == 0)
            continue;
        let img = urls[urls.length-1].file;
        if(results[i].webUrl != "" && results[i].id != "" && results[i].sectionId != "" && results[i].webTitle != "" && results[i].webPublicationDate != "" && results[i].webUrl != undefined && results[i].id != undefined && results[i].sectionId != undefined && results[i].webTitle != undefined && results[i].webPublicationDate != undefined){
            article["title"] = results[i].webTitle;
            article["section"] = results[i].sectionName;
            article["published_date"] = results[i].webPublicationDate;
             if(img === undefined || img === "undefined" || img === ""){
                article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
            }
            else{
                article["image"] = img;
            }
            article["id"] = results[i].id;
            article["url"] = results[i].webUrl;
            data[count] = article;
            count++;
        }
    }
    res.send(data);
    });
  });

  app.get('/guardianTechnology', (req, res) => {
    var data = [];
    request("https://content.guardianapis.com/technology?api-key=88c25341-0bf1-4241-a346-90a93f2090af&show-blocks=all&page-size=20", { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    let results = body.response.results;
    let count = 0;
    for(let i = 0; i < results.length; i++){
        if(count === 10)
            break;
        if(results[i] === undefined || results[i] === "undefined" || results[i] === null){
            continue;
        }

        let article = {};
        let a = results[i].blocks;
        if(a === null || a === "undefined" || a === undefined)
            continue;
        let b = a.main;
        if(b === null || b === "undefined" || b === undefined)
            continue;
        let c = b.elements[0];
        if(c === null || c === "undefined" || c === undefined)
            continue;
        let urls = c.assets;
        if(urls.length == 0)
            continue;
        let img = urls[urls.length-1].file;
        if(results[i].webUrl != "" && results[i].id != "" && results[i].sectionId != "" && results[i].webTitle != "" && results[i].webPublicationDate != "" && results[i].webUrl != undefined && results[i].id != undefined && results[i].sectionId != undefined && results[i].webTitle != undefined && results[i].webPublicationDate != undefined){
            article["title"] = results[i].webTitle;
            article["section"] = results[i].sectionName;
            article["published_date"] = results[i].webPublicationDate;
            if(img === undefined || img === "undefined" || img === ""){
                article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
            }
            else{
                article["image"] = img;
            }
            article["id"] = results[i].id;
            article["url"] = results[i].webUrl;
            data[count] = article;
            count++;
        }
    }
    res.send(data);
    });
  });

  app.get('/guardianSports', (req, res) => {
    var data = [];
    request("https://content.guardianapis.com/sport?api-key=88c25341-0bf1-4241-a346-90a93f2090af&show-blocks=all&page-size=20", { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    let results = body.response.results;
    let count = 0;
    for(let i = 0; i < results.length; i++){
        if(count === 10)
            break;
        if(results[i] === undefined || results[i] === "undefined" || results[i] === null){
            continue;
        }

        let article = {};
        let a = results[i].blocks;
        if(a === null || a === "undefined" || a === undefined)
            continue;
        let b = a.main;
        if(b === null || b === "undefined" || b === undefined)
            continue;
        let c = b.elements[0];
        if(c === null || c === "undefined" || c === undefined)
            continue;
        let urls = c.assets;
        if(urls.length == 0)
            continue;
        let img = urls[urls.length-1].file;
        if(results[i].webUrl != "" && results[i].id != "" && results[i].sectionId != "" && results[i].webTitle != "" && results[i].webPublicationDate != "" && results[i].webUrl != undefined && results[i].id != undefined && results[i].sectionId != undefined && results[i].webTitle != undefined && results[i].webPublicationDate != undefined){
            article["title"] = results[i].webTitle;
            article["section"] = results[i].sectionName;
            article["published_date"] = results[i].webPublicationDate;
            if(img === undefined || img === "undefined" || img === ""){
                article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
            }
            else{
                article["image"] = img;
            }
            article["id"] = results[i].id;
            article["url"] = results[i].webUrl;
            data[count] = article;
            count++;
        }
    }
    res.send(data);
    });
  });

  app.get('/guardianScience', (req, res) => {
    var data = [];
    request("https://content.guardianapis.com/science?api-key=88c25341-0bf1-4241-a346-90a93f2090af&show-blocks=all&page-size=20", { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    let results = body.response.results;
    let count = 0;
    for(let i = 0; i < results.length; i++){
        if(count === 10)
            break;
        if(results[i] === undefined || results[i] === "undefined" || results[i] === null){
            continue;
        }

        let article = {};
        let a = results[i].blocks;
        if(a === null || a === "undefined" || a === undefined)
            continue;
        let b = a.main;
        if(b === null || b === "undefined" || b === undefined)
            continue;
        let c = b.elements[0];
        if(c === null || c === "undefined" || c === undefined)
            continue;
        let urls = c.assets;
        if(urls.length == 0)
            continue;
        let img = urls[urls.length-1].file;
        if(results[i].webUrl != "" && results[i].id != "" && results[i].sectionId != "" && results[i].webTitle != "" && results[i].webPublicationDate != "" && results[i].webUrl != undefined && results[i].id != undefined && results[i].sectionId != undefined && results[i].webTitle != undefined && results[i].webPublicationDate != undefined){
            article["title"] = results[i].webTitle;
            article["section"] = results[i].sectionName;
            article["published_date"] = results[i].webPublicationDate
            if(img === undefined || img === "undefined" || img === ""){
                article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
            }
            else{
                article["image"] = img;
            }
            article["id"] = results[i].id;
            article["url"] = results[i].webUrl;
            data[count] = article;
            count++;
        }
    }
    res.send(data);
    });
  });

  app.get('/searchGuardian', (req, res) => {
      const query = url.parse(req.url,true).query;
    // console.log(query.query)

    var dataGuardian = [];
    request("https://content.guardianapis.com/search?q="+query.query+"&api-key=88c25341-0bf1-4241-a346-90a93f2090af&show-blocks=all&page-size=20", { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    let results = body.response.results;
    let count = 0;
    for(let i = 0; i < results.length; i++){
        if(count === 10)
                break;
        if(results[i] === undefined || results[i] === "undefined" || results[i] === null){
            continue;
        }
        let article = {};
        let a = results[i].blocks;
        if(a === null || a === "undefined" || a === undefined)
            continue;
        let b = a.main;
        if(b === null || b === "undefined" || b === undefined)
            continue;
        let c = b.elements[0];
        if(c === null || c === "undefined" || c === undefined)
            continue;
        let urls = c.assets;
        if(urls.length == 0)
            continue;
        let img = urls[urls.length-1].file;
        if(results[i].sectionId !== undefined && results[i].webTitle !== undefined && results[i].webPublicationDate !== undefined && img !== undefined || results[i].sectionId != "" && results[i].webTitle != "" && results[i].webPublicationDate != "" && img != ""){
            article["title"] = results[i].webTitle;
            article["section"] = results[i].sectionName;
            article["published_date"] = results[i].webPublicationDate;
            if(img === undefined || img === "undefined" || img === ""){
                article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
            }
            else{
                article["image"] = img;
            }
            article["id"] = results[i].id;
            article["url"] = results[i].webUrl;
            dataGuardian[count] = article;
            count++;
        }
    }
    console.log(dataGuardian);
    res.send(dataGuardian);
    });

  });

app.get('/detailGuardian', (req, res) => {
    const query = url.parse(req.url,true).query;
    request(encodeURI("https://content.guardianapis.com/"+query.id+"?api-key=88c25341-0bf1-4241-a346-90a93f2090af&show-blocks=all"), { json: true }, (err, response, body) => {
    if (err) { return console.log(err); }
    let result = body.response.content;
    let article = {};
    article["title"] = result.webTitle;
    if(result.blocks.main === null || result.blocks.main === undefined)
    	article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
    else{
    	if(result.blocks.main.elements[0] === null || result.blocks.main.elements[0] === undefined){
    	    article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"	
    	}
    	else{
		    let element = result.blocks.main.elements[0];
		    let img = "";
		    let assets = [];
		    if(element !== null)
		    	assets = element.assets;
		    if(assets.length > 0)
		    	if(assets[0].file !== null)
		    		img = assets[0].file;
		    if(img === undefined || img === "undefined" || img === ""){
		        article["image"] = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png"
		    }
		    else{
		        article["image"] = img;
		    }
		}
	}
    article["published_date"] = result.webPublicationDate;
    article["description"] = result.blocks.body[0].bodyHtml;
    article["url"] = result.webUrl;
    article["section"] = result.sectionName;
    article["id"] = result.id;
    res.send(article);
  });

});

app.listen(PORT, () => {
  console.log('Example app listening on port'+PORT);
});