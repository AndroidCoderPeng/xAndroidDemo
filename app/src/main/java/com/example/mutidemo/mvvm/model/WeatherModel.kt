package com.example.mutidemo.mvvm.model

data class WeatherModel(
    var code: String,
    var isCharge: Boolean,
    var msg: String,
    var result: ResultModelX
)

data class ResultModelX(
    var status: Int,
    var msg: String,
    var result: ResultModel
)

data class ResultModel(
    var city: String,
    var cityid: Int,
    var citycode: String,
    var date: String,
    var week: String,
    var weather: String,
    var temp: String,
    var temphigh: String,
    var templow: String,
    var img: String,
    var humidity: String,
    var pressure: String,
    var windspeed: String,
    var winddirect: String,
    var windpower: String,
    var updatetime: String,
    var aqi: AqiModel,
    var index: List<IndexModel>,
    var daily: List<DailyModel>,
    var hourly: List<HourlyModel>
)

data class AqiModel(
    var so2: String,
    var so224: String,
    var no2: String,
    var no224: String,
    var co: String,
    var co24: String,
    var o3: String,
    var o38: String,
    var o324: String,
    var pm10: String,
    var pm1024: String,
    var pm2_5: String,
    var pm2_524: String,
    var iso2: String,
    var ino2: String,
    var ico: String,
    var io3: String,
    var io38: String,
    var ipm10: String,
    var ipm2_5: String,
    var aqi: String,
    var primarypollutant: String,
    var quality: String,
    var timepoint: String,
    var aqiinfo: AqiinfoModel
)

data class AqiinfoModel(
    var level: String,
    var color: String,
    var affect: String,
    var measure: String
)

data class IndexModel(
    var iname: String,
    var ivalue: String,
    var detail: String
)

data class DailyModel(
    var date: String,
    var week: String,
    var sunrise: String,
    var sunset: String,
    var night: NightModel,
    var day: DayModel
)

data class NightModel(
    var weather: String,
    var templow: String,
    var img: String,
    var winddirect: String,
    var windpower: String
)

data class DayModel(
    var weather: String,
    var temphigh: String,
    var img: String,
    var winddirect: String,
    var windpower: String
)

data class HourlyModel(
    var time: String,
    var weather: String,
    var temp: String,
    var img: String
)