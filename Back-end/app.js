const express = require('express');
const fetch = require("node-fetch");
const googleTrends = require('google-trends-api');
const app = express();


app.use(function (req, res, next) {
    // Website you wish to allow to connect
    res.setHeader('Access-Control-Allow-Origin', '*');
    
    // Request methods you wish to allow
    res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS, PUT, PATCH, DELETE');

    // Request headers you wish to allow
    res.setHeader('Access-Control-Allow-Headers', 'X-Requested-With,content-type');

    // Set to true if you need the website to include cookies in the requests sent
    // to the API (e.g. in case you use sessions)
    res.setHeader('Access-Control-Allow-Credentials', true);

    // Pass to next layer of middleware
    next();
});

app.get('/api/guaridan/news/:section', (req, res, next) => {
    // Parse request url
    const section = req.params.section;
    const url = (section == 'all') ? 
      'https://content.guardianapis.com/search?order-by=newest&show-fields=starRating,headline,thumbnail,short-url&api-key=d5646984-678c-4605-836b-ce11bd849843' :
      'https://content.guardianapis.com/' + section + '?api-key=d5646984-678c-4605-836b-ce11bd849843&show-blocks=all';
   
    async function returnData() {
        try {
          let response = await fetch(url);
          let jsonText = await response.text();
          let rawJsonObj = await JSON.parse(jsonText);
          let parsedJsonObj = [];
          for(let i = 0; i < rawJsonObj.response.results.length; i++) {
            let imageUrl;
            try {
              imageUrl = (section == 'all') ? rawJsonObj.response.results[i].fields.thumbnail :
                                              rawJsonObj.response.results[i].blocks.main.elements[0].assets[0].file;
            } catch (error) {
            }
            if (imageUrl == null) {
              imageUrl = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
            }
            try {
              let temp = {id: rawJsonObj.response.results[i].id,
                          image: imageUrl,
                          title: rawJsonObj.response.results[i].webTitle,
                          time: rawJsonObj.response.results[i].webPublicationDate,
                          section: rawJsonObj.response.results[i].sectionName};
              parsedJsonObj.push(temp);
            }
            catch (error) {}
          }
          let jsonObj = JSON.stringify(parsedJsonObj);
          res.send(jsonObj);
        } catch (error) {
          console.error(error)  // handle error
        }
      }
      
    returnData()
});

app.get('/api/guaridan/article/:id', (req, res, next) => {
    // Parse request url
    const article_id = (req.params.id).replace(new RegExp('_', 'g'), '/');
    const url = 'https://content.guardianapis.com/' + article_id + '?api-key=d5646984-678c-4605-836b-ce11bd849843&show-blocks=all';
   
    async function returnData() {
      try {
        let response = await fetch(url);
        let jsonText = await response.text();
        let rawJsonObj = await JSON.parse(jsonText);
        let parsedJsonObj;
        let imageUrl;
        try {
          imageUrl = rawJsonObj.response.content.blocks.main.elements[0].assets[0].file;
        } 
        catch (error) {}
        if (imageUrl == null) {
          imageUrl = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
        }
        try {
          parsedJsonObj = {
                      image: imageUrl,
                      title: rawJsonObj.response.content.webTitle,
                      time: rawJsonObj.response.content.webPublicationDate,
                      section: rawJsonObj.response.content.sectionName,
                      description: rawJsonObj.response.content.blocks.body[0].bodyHtml,
                      url: rawJsonObj.response.content.webUrl
                    };
        }
        catch (error) {}
        let jsonObj = JSON.stringify(parsedJsonObj);
        res.send(jsonObj);
      } catch (error) {
        console.error(error)  // handle error
      }
    }
      
    returnData()
});

app.get('/api/guaridan/news/search/:keyword', (req, res, next) => {
    // Parse request url
    const keyword = req.params.keyword;
    const url = "https://content.guardianapis.com/search?q=" + keyword +"&api-key=d5646984-678c-4605-836b-ce11bd849843&show-blocks=all"
   
    async function returnData() {
        try {
          let response = await fetch(url);
          let jsonText = await response.text();
          let rawJsonObj = await JSON.parse(jsonText);
          let parsedJsonObj = [];
          for(let i = 0; i < rawJsonObj.response.results.length; i++) {
            let imageUrl;
            try {
              imageUrl = rawJsonObj.response.results[i].blocks.main.elements[0].assets[0].file;
            } catch (error) {
            }
            if (imageUrl == null) {
              imageUrl = "https://assets.guim.co.uk/images/eada8aa27c12fe2d5afa3a89d3fbae0d/fallback-logo.png";
            }
            try {
              let temp = {id: rawJsonObj.response.results[i].id,
                          image: imageUrl,
                          title: rawJsonObj.response.results[i].webTitle,
                          time: rawJsonObj.response.results[i].webPublicationDate,
                          section: rawJsonObj.response.results[i].sectionName};
              parsedJsonObj.push(temp);
            }
            catch (error) {}
          }
          let jsonObj = JSON.stringify(parsedJsonObj);
          res.send(jsonObj);
        } catch (error) {
          console.error(error)  // handle error
        }
      }
      
    returnData()
});

app.get('/api/trending/search/:keyword', (req, res, next) => {
  googleTrends.interestOverTime({keyword: req.params.keyword, startTime: new Date('2019-06-01')}, function(err, results) {
    if (err) {
      console.log('oh no error!', err);
    }
    else {
      let regexp = /value":\[\d*\]/gi;
      let matches_array = results.match(regexp);
      for(let i = 0; i < matches_array.length; i++) {
        matches_array[i] = matches_array[i].substring(8, matches_array[i].length-1);
      }
      let jsonObj = JSON.stringify(matches_array);
      res.send(jsonObj);
    }
  });
});


app.listen(process.env.PORT || 3000 , function () {
  console.log('app listening on port 3000!');
});