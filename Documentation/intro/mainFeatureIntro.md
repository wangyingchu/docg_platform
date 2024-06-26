###### Data Ocean & Cloud Graph  数海云图

***

# <span style="color:#CE0000;"> DOCG  </span> 数据分析平台概叙

##### ➜ 摘要 

> *DOCG 数据分析平台是一个以图数据（属性图）库做为核心数据存储系统，以领域模型（领域知识图谱）概念做为核心数据模型，以图数据分析以及内存实时计算做为核心技术能力的综合数据分析与AI算法平台。它能够综合利用关联关系分析、时间轴分析、地理空间分析等能力执行复杂的数据分析与知识发现工作。能够有效的应用在时空一体化分析，多维度关联数据分析以及 EDA 探索性数据分析等新兴的应用领域。*



DOCG 数据分析平台是一个技术领先的<span style="color:#001F3F;"> **综合数据分析与 AI 算法平台**</span>。它使用图数据库技术作为底层核心数据存储系统，能够提供较传统基于关系数据库的信息系统更加强大，更加先进的数据分析能力，从而助力业务数据的价值发现。平台能够将各种来源的业务数据融合到同一个数据网络中，并通过<span style="color:#001F3F;"> **时间**</span>，<span style="color:#001F3F;">**空间**</span>，<span style="color:#001F3F;">**关联关系**</span> 等维度，以易于理解的面向业务的视角，综合利用关联关系分析、时间分析、平面地理空间（GIS）分析以及AI算法等先进技术手段执行高度复杂的数据分析并发现隐含的知识。以下是DOCG 数据分析平台的独特功能：

- ##### <span style="color:#001F3F;"> 以 DDD 领域驱动设计为核心的领域模型构建功能</span>

- ##### <span style="color:#001F3F;">数字孪生的领域知识图谱</span>

- ##### <span style="color:#001F3F;"> 针对任何领域数据对象的关联性数据分析功能</span>

- ##### <span style="color:#001F3F;"> 针对任何领域数据对象的时空一体化分析功能</span>

- ##### <span style="color:#001F3F;">与主流大数据和机器学习，人工智能系统无缝集成</span>



#### 领域模型创建与管理

现代 IT 信息技术基础能力在持续的高速发展，与之相应，真实业务中客户需求的深度与广度也随之迅速提升，产生了越来越多的在企业全局视角下针对复杂场景的跨行业，跨业务线的高难度数据分析需求。这些分析需求涉及的各个行业的数据体系有其自身的业务独特性，也有跨行业间的共通性，需要一个科学合理的立足于整体的宏观数据模型体系来实现对各类繁乱复杂的业务数据的统一表述，管理和使用。

为了解决这个问题，需要有一个针对 **多源异构信息** 处理分析的数据管理系统，它必须具备一个核心能力：**能够使用统一的数据表达与管理手段，将各类不同信息系统中的数据组织，融合在一起**。为实现这种能力，该数据管理系统需要提供一种概念统一并且富有灵活性的建模能力，能够描述各种不同类型的外部数据源的技术与业务细节信息，并通过更高层的建模描述将这些信息对应的数据资源统合在一个业务场景中使用。此外它还必须提供一种统一的多元异构数据的获取与操作能力，能够将获取各类不同数据存储系统中业务数据的技术复杂性屏蔽，使用户只需要从业务领域模型的角度考虑业务应用问题，而无需关注具体的数据存取的技术细节。

DOCG 数据分析平台本质上就是这样的一个 **多源异构的信息存储，融合，访问，分析的IT数据处理系统，**它使用以下方法解决了上述问题：

1. 通过使用图数据库技术来构建数据管理领域模型，实现灵活的业务模型与关联关系定义。

2. 通过使用数据管理领域模型内置的标准多源异构数据映射功能将各种IT业务系统的数据源中的数据直接导入到 DOCG 数据分析平台。在不改变任何现有信息系统的前提下，透明的实现对不同尺度下的所有不同来源数据的统一管理和使用。

3. 通过在各种不同数据管理领域模型之间建立业务关联，使IT系统在数据层面上再现真实世界中复杂的业务关系（如时间关联，地理空间关联等）。进而利用各种高阶数据分析算法对数据进行有效率，有针对性的分析。

   

<span style="color:#0074D9;"> **使用 DDD 领域模型概念设计 DOCG 数据分析平台的核心能力应用逻辑**</span>

为了实现 **建模（Modeling）**这一核心特征，需要提出一套相应的领域模型概念，使 DOCG 数据分析平台能够以统一的逻辑和术语来兼容表述各类行业领域的所有不同业务信息，进而实现其 **抽象定义**，**真实映射** 和 **孪生分析** 的核心数据处理能力。以下是 DOCG 数据分析平台领域模型的核心概念：



一个对象类型（*ObjectClass*）可以和不限数量的 数据属性类型（*DataProperty*）相关联，这些关联定义了一个对象类型中能够包含的所有属性的详细定义。
	
针对一个对象类型（*ObjectClass*）能够创建不限数量的 对象实例（*ObjectInstance*）。这些对象实例中包含的数据种类严格遵循 对象类型中的定义。表示真实世界中的一个特定类型的事物的数字化镜像。

根据所属的对象类型中关联的数据属性类型（*DataProperty*），一个对象实例（*ObjectInstance*）中包含相应数量的属性（*DataProperty*）。
	
使用链接类型（*LinkClass*）来描述业务领域中需要表述的对象实例之间的各种不同性质、不同类型的抽象关联关系的定义。在一个业务领域中可以定义不限数量的链接类型来描述各类实体间的复杂抽象关系。使用链接实例（*LinkInstance*）来表达业务领域中真实存在的实体对象之间的实际关系。在一个业务领域中可以包含任意数量的链接实例。每一个链接实例都必须属于一个特定的链接类型。每一个链接实例都具有方向，必须指向一个源对象实例和一个目标对象实例。	

通过访问任何对象实例（*ObjectInstance*）均可获取到与其相关联的所有链接实例（*LinkInstance*），进而获取到通过该链接实例与其相关的另一个对象实例。
	
通过使用语义（*Semantic*）这一概念来表述业务领域中需要进行全局分析的各类业务维度（例如标签，字典等）。在一个业务领域中可以定义任意数量的语义。业务领域中的所有对象类型（*ObjectClass*）、对象实例（*ObjectInstance*）、链接类型（*LinkClass*）均可使用任何关系类型与这些分类建立关联关系。



<img src="DOCG-Term-ER.jpg" style="zoom:50%;" />











**使用图数据库技术为核心的 CIM 数据平台的技术设计**

CIM 数据平台旨在对 CIM 数据的对象定义、实例管理、对象关系管理、数据服务和数据相关配置等各方面进行综合、统一的应用管理。实现有效的统一管理之后，就可以对这些海量的城市数据进行孪生分析，既在数据层面上通过在各种不同 的 CIM 对象之间建立关联关系来再现真实世界中复杂的业务关联，进而对数据进行有效率有针对性的分析。图数据库是一种非常适应该场景的复杂关联数据的存储与分析技术。                      

图形数据库（Graph Database） 将整个数据集合建模成一个大型稠密的网络结构，使用图论来构建数据存取的模型。图论（Graph Theory）是数学的一个分支，它以图为研究对象。图论中的图是由若干给定的点及连接两点的线所构成的网状数据图形，这种图形通常用来描述某些事物之间的某种特定关系，用点代表事物，用连接两点的线表示相应两个事物间具有这种关系。而在当前的城市信息模型的应用中，有大量的对于相互关联的`关联数据 Relational Data`（例如包含、控制、接触、联络、群体依附和集结等）的查询和探索的需求，例如：

- 发现城市建筑网络中的关键影响因素、桥接实体和群体。 
- 智能发现建筑维修隐患以增强维修预警时效性并给出更明智、更专业的建议。
- 发现指示危险源头行为的模式及关系。 

针对这类“多对多对多”的关系查询和遍历计算以及定量分析，图论模型更是具有先天的优势。

此外使用图数据库技术实现的 CIM 数据平台领域模型具有以下两个独特的技术优点：

- 通过使⽤基于图形数据库的实现⽅式，可以消除关系模型在大规模数据集合和复杂数据类型及关联关系的应用场景下所无法解决的**实现复杂性**方面的问题。
- 基于图数据模型的特点以及图论算法的支持，对多维度条件下的关联信息查找和发现提供了比传统关系模型**更加强大的功能支持以及更加快捷的性能支持**。













以下是使用图数据库技术研发实现 CIM 数据平台的核心策略:

1. 通过使用图数据库来构建 CIM 数据平台领域模型中的各个逻辑组件，实现灵活的 CIM 模型与关联关系定义。
2. 通过使用 CIM 数据平台的领域模型将各种类型的异构数据源中的数据导入或映射到 CIM 数据平台，实现对数据的管理和使用。
3. 根据数据平台的领域模型定义，使用图数据库技术设计开发核心元数据与关联关系数据存储系统，其中存储城市信息模型中所有对象实体的元数据以及所有对象实体之间的关联关系。
4. 当对城市信息模型的对象实体进行基本存取和关联关系分析时，根据数据平台的领域模型定义直接操作核心元数据与关联关系数据存储系统。
5. 当对城市信息模型的对象实体进行复杂的跨外部异构数据源操作时，从核心元数据与关联关系数据存储系统中获取跨数据源操作所需的元数据信息，并以访问属性集的形式将外部异构数据源中的数据返回给用户。
6. 通过在各种不同 CIM 对象类型之间建立业务关联关系映射，使IT系统在数据层面上再现真实世界中复杂的业务关联（如时空一体化分析等），从而对数据进行有效率有针对性的分析。



**在基于图数据库技术的 CIM 数据平台上执行高阶知识图谱数据分析**

在使用图数据库技术的 CIM 数据平台中导入各类业务数据并完成数据关联后，实质上形成了一个智慧城市领域的知识图谱。通过使用该知识图谱，可以完成复杂的关联关系分析功能，从观察粒度看主要有两种分析途径：

- 微观层面：通过针对单个（或若干个）节点对象的关联关系分析，来发现局部数据中的知识信息，常用领域为EDA（探索性数据分析），推荐引擎（相似性分析），关联性查找（最短路径分析，关联性分析等）。
- 宏观层面：使用知识图谱技术，通过对图谱中所有或大量的节点对象和关系对象执行图论分析算法（PageRank，Triangle Count，Connected Components，Label Propagation等），来发现数据中整体性，规律性的全局知识。例如基于度分布的中枢节点发现，基于最大连通图的社区发现，基于三角计算的关系衡量，基于随机游走的用户属性传播等。

下图示例描述了在城市建设阶段针对建设工程管理的一个探索性数据分析的案例：













#### 数据计算网格







#### 时空一体化分析服务

时间与空间要素是实现城市场景多源数据融合的有效手段

时间，空间 和常规属性是所有智慧城市中业务数据本身固有的三种基本要素，是反映业务数据实体的状态和演变过程的重要组成部分。尽管不同来源中的数据使用的表达形式和技术实现方式各不相同，但在语义上是相同的。在知识图谱中通过使用时间和空间要素做为桥梁，可以将各种不同来源的数据有效的融合在一起。



















<img src="/media/wangychu/NSStorage1/GIT/DOCG/docg_platform/Documentation/intro/DOCG-Component-WholePicture.jpg" style="zoom: 50%;" />