import matplotlib.pyplot as plt
  import matplotlib.patches as mpatches
  from matplotlib.patches import FancyBboxPatch, FancyArrowPatch

  plt.rcParams['font.sans-serif'] = ['SimHei', 'Microsoft YaHei']
  plt.rcParams['axes.unicode_minus'] = False

  fig, ax = plt.subplots(figsize=(20, 16))
  ax.set_xlim(0, 20)
  ax.set_ylim(0, 16)
  ax.axis('off')

  def box(ax, x, y, w, h, text, color='#e8f5e9', edge='#2f6f62', fs=10, tc='#333'):
      b = FancyBboxPatch((x-w/2, y-h/2), w, h, boxstyle="round,pad=0.1",
                         facecolor=color, edgecolor=edge, linewidth=1.5)
      ax.add_patch(b)
      ax.text(x, y, text, ha='center', va='center', fontsize=fs, color=tc, fontweight='bold')

  def arrow(ax, x1, y1, x2, y2):
      ax.annotate('', xy=(x2, y2), xytext=(x1, y1),
                  arrowprops=dict(arrowstyle='->', color='#2f6f62', lw=1.5))

  # 起点
  box(ax, 10, 15, 4, 0.7, '用户进入AI智能服务模块', '#2f6f62', '#1a4a40', 12, 'white')

  # 五个功能入口
  funcs = ['AI对话', 'RAG知识库问答', '健康评估', '护理方案推荐', '知识库管理']
  fx = [2, 6, 10, 14, 18]
  fy = 13
  for i, name in enumerate(funcs):
      box(ax, fx[i], fy, 2.8, 0.6, name, '#fff3e0', '#e65100', 10)
      arrow(ax, 10, 14.65, fx[i], fy + 0.3)

  # AI对话流程
  a_steps = ['用户输入问题', '前端发送至\nAiController', 'LlmService调用\nMiMo大模型API', '返回回复内容', '前端打字动画\n逐字展示', 'localStorage\n持久化存储']
  for i, s in enumerate(a_steps):
      box(ax, 2, 11.5 - i*1.3, 2.5, 0.6, s, fs=8)
      if i == 0:
          arrow(ax, 2, 12.7, 2, 11.8)
      else:
          arrow(ax, 2, 11.5 - (i-1)*1.3 - 0.3, 2, 11.5 - i*1.3 + 0.3)

  # RAG流程
  b_steps = ['用户输入问题\n设定Top-K值', '问题向量化\nEmbedding API', 'SimpleVectorStore\n余弦相似度检索', '取Top-K条\n相关文档', '拼接RAG专用\nsystem prompt',
  '调用LLM\n生成回答', '前端展示回答']
  for i, s in enumerate(b_steps):
      box(ax, 6, 11.5 - i*1.1, 2.5, 0.6, s, fs=8)
      if i == 0:
          arrow(ax, 6, 12.7, 6, 11.8)
      else:
          arrow(ax, 6, 11.5 - (i-1)*1.1 - 0.3, 6, 11.5 - i*1.1 + 0.3)

  # 健康评估
  c_steps = ['选择目标老人', '填写健康信息', 'RAG检索知识库', 'LLM生成\n健康评估报告']
  for i, s in enumerate(c_steps):
      box(ax, 10, 11.5 - i*1.3, 2.5, 0.6, s, fs=8)
      if i == 0:
          arrow(ax, 10, 12.7, 10, 11.8)
      else:
          arrow(ax, 10, 11.5 - (i-1)*1.3 - 0.3, 10, 11.5 - i*1.3 + 0.3)

  # 护理方案推荐
  d_steps = ['选择目标老人', '填写当前状况\n和护理需求', 'RAG检索知识库', 'LLM生成\n护理方案建议']
  for i, s in enumerate(d_steps):
      box(ax, 14, 11.5 - i*1.3, 2.5, 0.6, s, fs=8)
      if i == 0:
          arrow(ax, 14, 12.7, 14, 11.8)
      else:
          arrow(ax, 14, 11.5 - (i-1)*1.3 - 0.3, 14, 11.5 - i*1.3 + 0.3)

  # 知识库管理
  e_steps = ['管理员上传文档', '按500字符分块\n50字符重叠', 'Embedding API\n生成向量', '存入\nSimpleVectorStore', '统计信息实时更新']
  for i, s in enumerate(e_steps):
      box(ax, 18, 11.5 - i*1.3, 2.5, 0.6, s, fs=8)
      if i == 0:
          arrow(ax, 18, 12.7, 18, 11.8)
      else:
          arrow(ax, 18, 11.5 - (i-1)*1.3 - 0.3, 18, 11.5 - i*1.3 + 0.3)

  # 降级处理
  box(ax, 10, 2.5, 6, 0.7, 'AI服务不可用时前端自动降级显示Mock演示数据', '#ffebee', '#c62828', 10, '#c62828')
  for x in [2, 6, 10, 14, 18]:
      arrow(ax, x, 3.5, 10, 2.85)

  plt.title('图4-5  AI智能服务业务流程图', fontsize=15, fontweight='bold', pad=20)
  plt.tight_layout()
  plt.savefig('AI智能服务业务流程图.png', dpi=300, bbox_inches='tight', facecolor='white')
  print('已生成：AI智能服务业务流程图.png')