###### Data Ocean & Cloud Graph  数海云图

***

# <span style="color:#CE0000;"> DOCG </span> 数据分析平台快速构建智慧城市数据底座（TOD应用方向）

##### ◎ 摘要 

> *DOCG 数据分析平台，能够将各种不同类型的数据导入到底层领域模型的概念类型中，通过在各类概念类型间持续的建立数据关联并与时间和空间维度绑定，即可便捷的构建出领域知识图谱。针对此知识图谱，DOCG 平台能够综合利用关联关系分析、时间轴分析、地理空间分析等能力执行复杂的数据分析与知识发现工作。本文将简要介绍一个使用 DOCG 平台构建北京市城市数据底座并将该底座与城市大型交通设施综合应用的案例。本文应用的技术手段能够普遍性的应用在各类 TOD（transit-oriented development，以公共交通为导向的开发）模式信息系统的建设开发中*。



智慧城市数据底座是智慧城市建设的基础和核心，它犹如城市的 “大脑”，负责处理和分析海量的城市数据。这些数据来自城市的各个方面，包括交通、环境、能源、公共设施、社会治安等。通过对这些数据的深入挖掘和分析，可以更加准确地了解城市的运行状况，预测未来发展趋势，制定科学合理的政策和措施。**CIM** (City Information Modeling) 城市信息模型是目前构建城市数据底座的一个主要方式。CIM 城市信息模型是一个概念与方法的理论集成，把城市各类多源异构数据以多种形式整体组织成一个和实体城市全息镜像的城市体系信息模型。它由城市主体的建筑物出发，基于空间关系的关联性，将基础的地理信息、空间坐标系统与具体的城市空间格局应用相结合，形成了以地理信息系统（**GIS**）为底层基础，以建筑信息模型（**BIM**）为中层填充，以数字孪生（**Digital Twin**）技术、物联网（**IoT**）技术为抓手，将城市空间维度的地上地下、室内室外、时间维度的过往历史、当前现状和可期未来的多维信息模型数据和城市感知数据相融合的数字空间城市部件要素信息有机综合体。DOCG 数据分析平台具有构建一个 CIM 模式智慧城市数据底座的全部技术能力。本文通过以下步骤建立一个小型的城市数据底座，并结合一个大型机场的建筑信息与运营信息，介绍一个典型的TOD类的应用场景。

##### 1. 通过将城市 GIS 数据导入 DOCG 领域模型构建数据底座底层城市基础设施信息模型。
##### 2. 通过将大型机场 BIM 数据导入 DOCG 领域模型构建中层智慧建筑应用支持信息模型。
##### 3. 通过将大型机场 动态业务数据 实时导入领域模型构建上层业务应用信息模型。
#####  4. 综合应用已经构建的城市数据底座与 DOCG 平台的分析能力创建 示例 TOD类智慧城市应用



#### ※ 导入GIS数据构建数据底座底层城市基础设施信息模型

GIS 地理空间数据是构建智慧城市基底信息常用的数据来源。使用已有的 GIS 数据可以轻易的构建出大规模的城市基础设施信息模型。DOCG  平台内建对 SHP（ESRI shape）格式地理信息数据导入的能力。支持全球，全国以及局部三种不同空间尺度的地理信息数据的直接导入，能够直接将 SHP 数据导入领域模型中的概念类型定义中，并保持全部的业务属性与地理空间属性。除各类常规属性的检索查询应用外，从 SHP 数据转化生成的概念实体数据能够参与 DOCG 平台中的全部地理空间分析计算应用。

从 DOCG 数据分析平台的用户界面中可以通过以下步骤导入 GIS 数据，下文以导入北京市 *水系-湖，库* 数据为例 :

1. 在<span style="color:#0074D9;"> *Conception Kind 概念类型数据管理* </span>  界面中点击  [*创建概念类型* ] 按钮 创建 概念类型  **RiverSystem-Surface ( 水系-湖，库 ) **

2. 在概念类型定义列表中选中概念类型 RiverSystem-Surface，点击  [*配置概念类型定义* ] 按钮，打开  <span style="color:#0074D9;"> *概念类型配置* </span>  界面

3. 点击  [*导入概念实体数据* ] -》 [*SHP格式数据* ] 按钮，选择并上传 zip 格式的 SHP 数据压缩包，即可将 SHP 文件中的全部地理空间数据作为概念实体导入概念类型 RiverSystem-Surface

以下为相关界面操作截图：

###### 概念类型数据管理界面
<div style="text-align:left;">
    <img src="ConceptionKindDataManage.png" alt="Your Image" style="display: block; margin-left: 0; margin-right: auto;"/>
</div>



###### 概念类型配置界面

<div style="text-align:left;">
    <img src="ImportSHP.png" alt="Your Image" style="display: block; margin-left: 0; margin-right: auto;"/>
</div>




<div style="text-align:left;">
    <img src="RiverSystemEntity.png" alt="Your Image" style="display: block; margin-left: 0; margin-right: auto;"/>
</div>


<div style="text-align:left;">
    <img src="AllRiverSystemEntities.png" alt="Your Image" style="display: block; margin-left: 0; margin-right: auto;"/>
</div>



从 DOCG 数据分析平台的用户界面中可以通过以下步骤导入 GIS 数据，下文以导入北京市 *水系-湖，库* 数据为例










<div style="display: flex;">
    <img src="GIS_level1.png" alt="Image 1" style="margin-right: 2px;zoom:12.5%;">
    <img src="GIS_level2.png" alt="Image 2" style="margin-right: 1px;zoom:12.5%;"">
    <img src="GIS_level3.png" alt="Image 3" style="margin-left: 1px;zoom:12.5%;">
</div>








<div style="text-align:right;">
    <img src="hero-visual.svg" alt="Your Image" style="display: block; margin-left: auto; margin-right: 0;zoom:25%;"/>
</div>