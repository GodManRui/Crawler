var requestMap = new map();
$(function () {
    var reqParam;
    reqParam = getParam();
    var param = new Object();
    param.request = new Object();
    param.request.guid = reqParam.guId;
    param.request.businessType = "alertLogDetail";
    param.callback = "onPipeData";
    param.code = "query_server_data@" + param.request.businessType + guid();
    sendRequest(param.code, param, alertCallBack, reqParam.os);

});

/**
 * 绘制报警信息的横向Tab切换栏下的所有内容
 * @param heads 表头信息，报警指标 的集合
 * @param datas 数据行信息   表头对应的数据
 * @returns {*|jQuery|HTMLElement}
 */
function drawTabContent(heads, datas) {
    let allContent = datas[0];              //data -> 1 - 12 条内容
    const headLength = heads.length;
    //绘制最外部边框容器             整个Tab页面的内容容器
    const frameSwiper = $("<div class=\"swiper-slide\"></div>");
    const frameAlertCount = $("<div class=\"alertCont\"></div>");
    //创建内容加标题的外部容器
    var requestTop;
    //创建内容的外部容器                         +3
    var requestDiv;
    let isAdd = false;
    //创建每个小页签
    for (let i = 0; i < allContent.length; i++) {

        //创建当前小页签内容 小标题+内容  的外部层级     +1

        if (i % headLength === 0) {
            //判断是不是第一条，如果第一条，marginTop比较小，class不一样
            requestTop = $(i === 0 ? "<div class= \"requestTop\"> </div>" : "<div class=\"requestTop mtw\"> </div>");
            //创建内容的外部容器                         +3
            requestDiv = $("<div class=\"requestDiv cl\"> </div>");
        }

        //提取内容
        let currentHead = heads[i % headLength];    //提取当前标签名字   报警名称 or 平均响应时间 or 最大响应时间
        let contentData = allContent[i];            //提取数据  是一个对象   "alert_level": "", "alert_value": "我是一个有身份的报警", "alert_threshold": ""


        //如果有名字，得把名字提出来单做一个层级
        if (contentData.alert_level === "" && contentData.alert_threshold === "") {
            //有头部信息
            isAdd = true;
            let titleHtml = drawTitle(contentData.alert_value);
            requestTop.append(titleHtml);
            continue;
        }

        const contentHtml = drawContent(contentData, currentHead);
        requestDiv.append(contentHtml);                             //添加每个指标小块
        if ((isAdd ? i + 1 : i) % headLength % 2 === 0) {
            requestDiv.append($("<span class = \"requestBorder\"/>"));  //加一道分割线
        }
        // 当前指标项目块已经结束
        if ((i + 1) % headLength === 0) {
            isAdd = false;
            requestTop.append(requestDiv);
            frameAlertCount.append(requestTop);
            requestTop = null;
            requestDiv = null;
        }
    }
    frameSwiper.append(frameAlertCount);
    return frameSwiper;
}

/**
 * 创建此报警指标项目的小标题
 * @param titleName 标题名称
 * @returns {*|jQuery|HTMLElement}  标题内容的html元素
 */
function drawTitle(titleName) {
    return $("<div class=\"requestUrl requestUrlHgt pos\"><font class=\"requestUrlWid\"  >" + titleName + "</font></div>");
}

/**
 * 创建每个指标小块
 * @param contents  指标块的内容集合 一般内含三个参数
 * @param describe  指标名字
 * @returns {*}     小指标快的html元素
 */
function drawContent(contents, describe) {

    const value = contents.alert_value;
    const unit = contents.alert_threshold;
    const level = contents.alert_level;

    let frame;
    let content;
    if (level === "0") {
        //橘色
        // orangeData  redData                     orangeWord  redWord
        frame = $("<div class=\"requestData orangeData requestDataWid2\"></div>");
        content = $("<h3 id=\"total_count\">" + value + "<span>" + unit + "</span>" + "</h3><p>" + describe + "<span class='orangeWord'>" + unit + "</span>");
    } else {
        //红色
        frame = $("<div class=\"requestData redData requestDataWid2\"></div>");
        content = $("<h3 id=\"total_count\">" + value + "<span>" + unit + "</span>" + "</h3><p>" + describe + "<span class='redWord'>" + unit + "</span>");
    }
    frame.append(content);
    return frame;
}

/**
 * swiper控件初始化
 */
function setSwiper() {

    var menuSwiper = $("#tabMenu").swiper({
        speed: 1000,
        slidesPerView: "auto",	//显示的slide个数
        spaceBetween: 30,	//slide间隔
        watchSlidesProgress: true,		//计算每个slide的progress
        watchSlidesVisibility: true,	//开启了watchSlidesVisibility，则会在每个可见slide增加一个classname，默认为'swiper-slide-visible'。

        onTap: function (swiper) {	//回调函数，当你轻触(tap)Swiper后执行。
            tabSwiper.slideTo(menuSwiper.clickedIndex, 500);
        }
    });

    //显示多个slide的情况下，swiper.activeIndex总是在最左边的第一个
    var tabSwiper = $("#tabContent").swiper({
        speed: 1000,
        spaceBetween: 30,	//slide间隔
        onSlideChangeStart: function (swiper) {	//回调函数：当前slide过度到下一个slide时执行
            //alert(tabSwiper.activeIndex);
            $(".menuBox li.on").removeClass("on");
            $(".menuBox li").eq(tabSwiper.activeIndex).addClass("on");	//这两行执行tab切换

            var activeNav = $("#tabMenu .swiper-slide").eq(tabSwiper.activeIndex);	//菜单中当前的slide（加上‘on’类后的slide）
            if (!activeNav.hasClass("swiper-slide-visible")) {	//菜单中当前的slide没有swiper-slide-visible这个类说明他被隐藏了（没有显示）
                //console.log(1);
                if (activeNav.index() > menuSwiper.activeIndex) {		//说明菜单当前的slide隐藏在最右边
                    //console.log(2);
                    var thumbsPerNav = Math.floor(menuSwiper.width / activeNav.width()) - 1;
                    menuSwiper.slideTo(activeNav.index() - thumbsPerNav);
                } else {
                    //console.log(3);
                    menuSwiper.slideTo(activeNav.index());
                }
            }
        }
    });
}

/**
 * 报警类型名字拼接
 * @param alert_leave   报警等级
 * @returns {string}
 */
function createAlertName(alert_message) {
    let alertName = "";
    for (let i = 0; i < alert_message.length; i++) {
        const alertMessageElement = alert_message[i];
        if (i !== 0)
            alertName += ",";
        alertName += alertMessageElement.alert_type;
    }
    return alertName;
}

function alertCallBack() {
    let response;

    if (this.state) {                                                                  // 数据如果有异常，则显示无数据图
        response = eval("(" + this.responseJson + ")");
        if (response.code === 0) {

            try {
                const data = $.parseJSON(response.response.data.logDetail);
                if (data !== undefined || data.alert_message.length !== 0) {
                    var alertName = createAlertName(data.alert_message);

                    $("#alertName").text(data.alert_name);                     //设置报警名称
                    $("#appName").text(data.app_name);                           //设置应用名称
                    $("#alertType").text(alertName);                      //设置报警类型

                    /* let startTime = new Date(data.alert_start_time).Format("yyyy-MM-dd hh:mm:ss"); IOS无法转换，暂时注释
                     let endTime = new Date(data.alert_end_time).Format("yyyy-MM-dd hh:mm:ss");*/
                    $("#alertTime").text(data.alert_start_time + " - " + data.alert_end_time);    //设置报警时间

                    const alertMessage = data.alert_message;
                    //alertMessage.length  决定有几个报警模块，横向Tab栏的条目
                    for (let i = 0; i < alertMessage.length; i++) {
                        //外层循环， 报警名称，Tab栏
                        const alertMessageDate = alertMessage[i];                        // 每个报警模块
                        let tag = i === 0 ? "<li class='" + "on swiper-slide'" + ">"    //创建当前报警Tab标题栏
                            : "<li class=" + "'swiper-slide'" + ">";
                        const tagName = alertMessageDate.alert_type;                   //当前报警的名字
                        $("#tabTitle").append(tag + tagName + "</li>");                 //将这个Tab添加进内容容器里
                        // alertType = alertType + alertMessageDate.alert_type;

                        //内层循环， 报警内容
                        //当前报警内容 ， 头部集合
                        const content = drawTabContent(alertMessageDate.head, alertMessageDate.data);
                        $("#divContent").append(content);
                    }
                }
            } catch (e) {
                appLog(e);
            }
        }
    }
    setSwiper();
}
