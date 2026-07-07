export const fieldMap = {
  users: [
    ['phone', '手机号', 'text', true],
    ['password', '密码', 'password', false],
    ['name', '姓名', 'text', true],
    ['age', '年龄', 'number', true],
    ['gender', '性别', 'select', true, ['男', '女'], '男'],
    ['role', '角色', 'select', true, [
      { label: '管理员', value: 'ROLE_ADMIN' },
      { label: '健康管家', value: 'ROLE_MANAGER' },
      { label: '护士', value: 'ROLE_NURSE' },
      { label: '入住老人', value: 'ROLE_USER' }
    ], 'ROLE_USER'],
    ['enabled', '状态', 'select', true, [
      { label: '正常', value: 1 },
      { label: '禁用', value: 0 }
    ], 1]
  ],
  customers: [
    ['name', '姓名', 'text', true],
    ['gender', '性别', 'select', true, ['男', '女'], '男'],
    ['age', '年龄', 'number', true],
    ['phone', '联系电话', 'text'],
    ['emergencyContact', '紧急联系人', 'text'],
    ['checkInDate', '入住日期', 'date'],
    ['healthStatus', '健康状态', 'text'],
    ['remark', '备注', 'textarea'],
    ['checkedIn', '是否入住', 'select', false, [
      { label: '已入住', value: 1 },
      { label: '未入住', value: 0 }
    ], 0]
  ],
  beds: [
    ['roomNo', '房间编号', 'text', true],
    ['bedNo', '床位编号', 'text', true],
    ['status', '状态', 'select', true, ['空闲', '已入住', '维修中'], '空闲']
  ],
  'check-ins': [
    ['customerId', '客户姓名', 'select', true],
    ['bedId', '床位', 'select', true],
    ['checkInDate', '入住日期', 'date', true],
    ['contractMonths', '合同期限(月)', 'number'],
    ['deposit', '押金', 'number'],
    ['operator', '经办人', 'select', false]
  ],
  'check-outs': [
    ['customerId', '客户姓名', 'select', true],
    ['checkoutDate', '退住日期', 'date', true],
    ['reason', '原因', 'textarea'],
    ['operator', '经办人', 'select', false]
  ],
  outings: [
    ['customerId', '客户姓名', 'select', true],
    ['outTime', '外出时间', 'datetime', true],
    ['expectedReturnTime', '预计返回时间', 'datetime'],
    ['remark', '备注', 'textarea'],
    ['status', '状态', 'select', false, ['外出中', '已返回'], '外出中']
  ],
  meals: [
    ['customerId', '客户姓名', 'select', true],
    ['breakfast', '早餐', 'text'],
    ['lunch', '午餐', 'text'],
    ['dinner', '晚餐', 'text'],
    ['specialNeed', '特殊需求', 'textarea'],
    ['mealDate', '日期', 'date', true],
    ['mealImg', '膳食图片', 'image', false]
  ],
  'service-relations': [
    ['customerId', '客户姓名', 'select', true],
    ['managerId', '健康管家', 'select', true]
  ],
  'care-services': [
    ['serviceName', '服务名称', 'text', true],
    ['price', '价格', 'number', true],
    ['content', '内容', 'textarea'],
    ['period', '周期', 'text']
  ],
  'service-purchases': [
    ['customerId', '客户姓名', 'select', true],
    ['serviceId', '服务名称', 'select', true],
    ['purchaseDate', '购买日期', 'date', true],
    ['status', '状态', 'select', false, ['有效', '已结束'], '有效']
  ],
  'nursing-levels': [
    ['levelName', '级别名称', 'text', true],
    ['description', '说明', 'textarea'],
    ['fee', '收费标准', 'number', true]
  ],
  'nursing-items': [
    ['itemName', '护理项目名称', 'text', true],
    ['description', '说明', 'textarea'],
    ['levelId', '护理级别', 'select', true],
    ['frequency', '执行频率', 'select', false, ['每日1次', '每2小时1次', '每周1次', '隔日一次', '每月1次'], '每日1次']
  ],
  'nursing-records': [
    ['customerId', '客户姓名', 'select', true],
    ['itemId', '护理项目', 'select', true],
    ['nurseName', '护理人员', 'select', true],
    ['nursingTime', '护理时间', 'datetime', true],
    ['result', '结果', 'select', false, ['正常', '异常', '已完成'], '正常'],
    ['remark', '备注', 'textarea']
  ],
  'nurse-areas': [
    ['areaName', '区域名称', 'text', true],
    ['remark', '备注', 'textarea']
  ]
}

// ===================== 额外表格展示列（后端自动生成，不在表单里） =====================
export const getExtraColumns = (resource) => {
  const config = {
    customers: [['customerNo', '客户编号']],
    beds: [['customerName', '入住客户']],
    'check-ins': [['registerNo', '登记编号']],
    'check-outs': [['checkoutNo', '退住编号']],
    outings: [['outingNo', '外出编号']],
    meals: [['mealNo', '餐食编号']] 
  }
  return (config[resource] || []).map(([prop, label]) => ({ prop, label }))
}

// ===================== 下拉框数据源 =====================
export const dropdownSources = {
  'check-ins': [
    { prop: 'customerId', resource: 'customers', label: 'name', value: 'id', params: { checkedIn: 0 }, filterField: 'checkedIn', filterValue: 0, filterLabel: '已入住' },
    { prop: 'bedId', resource: 'beds', label: 'bedNo', value: 'id', params: { status: '空闲' }, filterField: 'status', filterValue: '已入住', filterLabel: '已占用' },
    { prop: 'operator', resource: 'users', label: 'name', value: 'name' }
  ],
  'check-outs': [
    { prop: 'customerId', resource: 'customers', label: 'name', value: 'id', params: { checkedIn: 1 }, filterField: 'checkedIn', filterValue: 0, filterLabel: '未入住' },
    { prop: 'operator', resource: 'users', label: 'name', value: 'name' }
  ],
  outings: [
    { prop: 'customerId', resource: 'customers', label: 'name', value: 'id', params: { checkedIn: 1 }, filterField: 'checkedIn', filterValue: 0, filterLabel: '未入住' }
  ],
  meals: [
    { prop: 'customerId', resource: 'customers', label: 'name', value: 'id' }
  ],
  'service-relations': [
    { prop: 'customerId', resource: 'customers', label: 'name', value: 'id' },
    { prop: 'managerId', resource: 'users', label: 'name', value: 'id', params: { role: 'ROLE_MANAGER' } }
  ],
  'service-purchases': [
    { prop: 'customerId', resource: 'customers', label: 'name', value: 'id' },
    { prop: 'serviceId', resource: 'care-services', label: 'serviceName', value: 'id' }
  ],
  'nursing-items': [
    { prop: 'levelId', resource: 'nursing-levels', label: 'levelName', value: 'id' }
  ],
  'nursing-records': [
    { prop: 'customerId', resource: 'customers', label: 'name', value: 'id' },
    { prop: 'itemId', resource: 'nursing-items', label: 'itemName', value: 'id' },
    { prop: 'nurseName', resource: 'users', label: 'name', value: 'name', params: { role: 'ROLE_NURSE' } }
  ]
}

// ===================== 搜索提示 =====================
export const searchHints = {
  users: '搜索姓名或手机号',
  customers: '搜索姓名、编号或手机号',
  beds: '搜索房间号或床位号',
  'check-ins': '搜索登记编号或经办人',
  'check-outs': '搜索退住编号或经办人',
  outings: '搜索外出登记编号',
  meals: '搜索餐食编号',
  'service-relations': '搜索关联记录',
  'care-services': '搜索服务名称或内容',
  'service-purchases': '搜索服务名称',
  'nursing-levels': '搜索级别名称',
  'nursing-items': '搜索项目名称',
  'nursing-records': '搜索记录编号',
  'nurse-areas': '搜索区域名称'
}

// ===================== 辅助函数 =====================
export const toFields = (resource) =>
  (fieldMap[resource] || []).map(([prop, label, type = 'text', required = false, options = [], defaultValue = null]) => ({
    prop, label, type, required, options, default: defaultValue
  }))

export const roleFormatter = (role) => {
  const map = { ROLE_ADMIN: '管理员', ROLE_MANAGER: '健康管家', ROLE_NURSE: '护士', ROLE_USER: '入住老人' }
  return map[role] || role
}

export const enabledFormatter = (enabled) => enabled === 1 ? '正常' : '禁用'

export const checkedInFormatter = (checkedIn) => checkedIn === 1 ? '已入住' : '未入住'
