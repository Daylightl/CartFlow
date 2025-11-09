# 图片资源说明

本项目需要以下图片资源。由于实际图片文件较大，这里仅提供占位说明。

## 商品图片（必需）

请在 `products/thumbs/` 目录下放置以下图片：

### P001_thumb.jpg
- **尺寸**: 400x400px
- **格式**: JPG
- **用途**: 电子产品（手机、图书等）
- **建议**: 可以使用手机、电子产品的图片

### P002_thumb.jpg
- **尺寸**: 400x400px
- **格式**: JPG
- **用途**: 配件类商品（耳机、零食等）
- **建议**: 可以使用耳机或其他配件的图片

### P003_thumb.jpg
- **尺寸**: 400x400px
- **格式**: JPG
- **用途**: 服装类商品（T恤等）
- **建议**: 可以使用T恤或其他服装的图片

## 获取图片的方式

### 方式1: 使用占位图片服务
您可以使用在线占位图片服务生成临时图片：

```bash
# 使用 wget 或 curl 下载占位图片
wget https://via.placeholder.com/400x400.jpg/09f/fff -O products/thumbs/P001_thumb.jpg
wget https://via.placeholder.com/400x400.jpg/f90/fff -O products/thumbs/P002_thumb.jpg
wget https://via.placeholder.com/400x400.jpg/0f9/fff -O products/thumbs/P003_thumb.jpg
```

### 方式2: 使用免费图片网站
- [Unsplash](https://unsplash.com/) - 高质量免费图片
- [Pexels](https://www.pexels.com/) - 免费商用图片
- [Pixabay](https://pixabay.com/) - 免费图片和视频

### 方式3: 使用AI生成工具
可以使用以下AI工具生成商品图片：
- Midjourney
- DALL-E
- Stable Diffusion

## 快速设置（推荐）

如果您只是想快速测试系统，可以使用纯色占位图：

```bash
cd src/main/resources/static/images/products/thumbs/

# 使用 ImageMagick 创建纯色占位图（如果已安装）
convert -size 400x400 xc:#409EFF P001_thumb.jpg
convert -size 400x400 xc:#67C23A P002_thumb.jpg
convert -size 400x400 xc:#E6A23C P003_thumb.jpg
```

或者使用在线服务：
```bash
curl "https://via.placeholder.com/400/409EFF/FFFFFF?text=Product" > P001_thumb.jpg
curl "https://via.placeholder.com/400/67C23A/FFFFFF?text=Product" > P002_thumb.jpg
curl "https://via.placeholder.com/400/E6A23C/FFFFFF?text=Product" > P003_thumb.jpg
```

## 注意事项

1. 图片文件名必须与上述完全一致（区分大小写）
2. 建议图片大小不超过 500KB，以提高加载速度
3. 如果缺少图片，系统会显示默认占位图或加载失败
4. 图片路径在代码中已配置为 `/images/products/thumbs/`

## 可选图片（暂未使用）

以下图片位置已预留，但当前版本暂未使用，可以忽略：

- `banners/` - 首页轮播图
- `categories/` - 分类图标
- `empty-states/` - 空状态插图
- `ui/` - Logo 和其他UI元素

如需使用这些功能，请参考项目 PRD 文档中的图片规范。
