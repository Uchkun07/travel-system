# 轮播图 API 文档

## 基础信息

- **Base URL**: `/slideshow`
- **返回格式**: JSON (统一使用 ApiResponse)

## API 接口列表

### 1. 获取启用的轮播图 (前台展示)

**接口**: `GET /slideshow/active`

**描述**: 获取所有启用且在有效时间范围内的轮播图,按显示顺序排序

**请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "获取轮播图成功",
  "data": [
    {
      "slideshowId": 1,
      "title": "故宫博物院",
      "subtitle": "探索紫禁城的千年历史",
      "imageUrl": "https://example.com/images/forbidden-city.jpg",
      "attractionId": 1,
      "displayOrder": 1,
      "status": 1,
      "startTime": "2024-01-01T00:00:00",
      "endTime": "2024-12-31T23:59:59",
      "clickCount": 0,
      "createTime": "2024-01-01T00:00:00",
      "updateTime": "2024-01-01T00:00:00"
    }
  ]
}
```

---

### 2. 记录轮播图点击

**接口**: `POST /slideshow/click/{id}`

**描述**: 记录用户点击轮播图,增加点击计数

**路径参数**:

- `id` (Integer): 轮播图 ID

**响应示例**:

```json
{
  "code": 200,
  "message": "点击记录成功",
  "data": null
}
```

---

### 3. 获取所有轮播图 (管理后台)

**接口**: `GET /slideshow/list`

**描述**: 获取所有轮播图(包括已禁用的),按显示顺序排序

**请求参数**: 无

**响应示例**:

```json
{
  "code": 200,
  "message": "获取轮播图列表成功",
  "data": [
    // 轮播图对象数组
  ]
}
```

---

### 4. 获取轮播图详情

**接口**: `GET /slideshow/{id}`

**描述**: 根据 ID 获取单个轮播图的详细信息

**路径参数**:

- `id` (Integer): 轮播图 ID

**响应示例**:

```json
{
  "code": 200,
  "message": "获取轮播图详情成功",
  "data": {
    "slideshowId": 1,
    "title": "故宫博物院"
    // ... 其他字段
  }
}
```

---

### 5. 添加轮播图

**接口**: `POST /slideshow/add`

**描述**: 添加新的轮播图

**请求体**:

```json
{
  "title": "九寨沟风光",
  "subtitle": "人间仙境,五彩斑斓",
  "imageUrl": "https://example.com/images/jiuzhaigou.jpg",
  "attractionId": 5,
  "displayOrder": 5,
  "status": 1,
  "startTime": "2024-05-01T00:00:00",
  "endTime": "2024-10-31T23:59:59"
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "添加轮播图成功",
  "data": null
}
```

---

### 6. 更新轮播图

**接口**: `PUT /slideshow/update`

**描述**: 更新轮播图信息

**请求体**:

```json
{
  "slideshowId": 1,
  "title": "故宫博物院(更新)",
  "subtitle": "探索紫禁城的千年历史",
  "imageUrl": "https://example.com/images/forbidden-city-new.jpg",
  "attractionId": 1,
  "displayOrder": 1,
  "status": 1
}
```

**响应示例**:

```json
{
  "code": 200,
  "message": "更新轮播图成功",
  "data": null
}
```

---

### 7. 更新轮播图状态

**接口**: `PUT /slideshow/status/{id}`

**描述**: 启用或禁用轮播图

**路径参数**:

- `id` (Integer): 轮播图 ID

**查询参数**:

- `status` (Integer): 状态值 (0=禁用, 1=启用)

**示例**: `PUT /slideshow/status/1?status=0`

**响应示例**:

```json
{
  "code": 200,
  "message": "更新状态成功",
  "data": null
}
```

---

### 8. 批量更新显示顺序

**接口**: `PUT /slideshow/order`

**描述**: 批量更新多个轮播图的显示顺序

**请求体**:

```json
[
  {
    "slideshowId": 1,
    "displayOrder": 2
  },
  {
    "slideshowId": 2,
    "displayOrder": 1
  },
  {
    "slideshowId": 3,
    "displayOrder": 3
  }
]
```

**响应示例**:

```json
{
  "code": 200,
  "message": "更新显示顺序成功",
  "data": null
}
```

---

### 9. 删除轮播图

**接口**: `DELETE /slideshow/delete/{id}`

**描述**: 删除指定的轮播图

**路径参数**:

- `id` (Integer): 轮播图 ID

**响应示例**:

```json
{
  "code": 200,
  "message": "删除轮播图成功",
  "data": null
}
```

---

## 数据字段说明

### Slideshow 对象

| 字段         | 类型     | 说明                     | 必填         |
| ------------ | -------- | ------------------------ | ------------ |
| slideshowId  | Integer  | 轮播图唯一标识           | 否(自增)     |
| title        | String   | 轮播图标题               | 是           |
| subtitle     | String   | 副标题/描述              | 否           |
| imageUrl     | String   | 轮播图图片 URL           | 是           |
| attractionId | Integer  | 关联的景点 ID            | 否           |
| displayOrder | Integer  | 显示顺序(数字越小越靠前) | 否(默认 0)   |
| status       | Integer  | 状态 (0=禁用, 1=启用)    | 否(默认 1)   |
| startTime    | DateTime | 开始展示时间             | 否           |
| endTime      | DateTime | 结束展示时间             | 否           |
| clickCount   | Integer  | 点击次数                 | 否(自动计数) |
| createTime   | DateTime | 创建时间                 | 否(自动填充) |
| updateTime   | DateTime | 更新时间                 | 否(自动更新) |

## 使用场景

### 前台展示

```javascript
// 获取轮播图数据
fetch("/slideshow/active")
  .then((res) => res.json())
  .then((data) => {
    if (data.code === 200) {
      // 渲染轮播图
      renderSlideshow(data.data);
    }
  });

// 记录点击
function onSlideshowClick(slideshowId) {
  fetch(`/slideshow/click/${slideshowId}`, {
    method: "POST",
  });
}
```

### 后台管理

```javascript
// 获取所有轮播图
fetch("/slideshow/list")
  .then((res) => res.json())
  .then((data) => {
    // 展示管理列表
  });

// 启用/禁用
function toggleStatus(id, status) {
  fetch(`/slideshow/status/${id}?status=${status}`, {
    method: "PUT",
  });
}

// 拖拽排序后更新顺序
function updateOrder(slideshows) {
  fetch("/slideshow/order", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(slideshows),
  });
}
```

## 注意事项

1. **时间范围控制**: `startTime` 和 `endTime` 用于控制轮播图的展示时间,前台接口会自动过滤不在时间范围内的轮播图
2. **状态控制**: 只有 `status=1` 的轮播图才会在前台展示
3. **显示顺序**: `displayOrder` 数字越小越靠前,建议使用 10, 20, 30... 的间隔,便于插入新项
4. **外键关联**: `attractionId` 关联到 `attraction` 表,删除景点时会自动将关联的轮播图的 `attractionId` 设为 NULL
5. **跨域配置**: Controller 已添加 `@CrossOrigin` 注解支持跨域请求
