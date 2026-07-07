<template>
  <el-card class="page-card fade-up">
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input v-model="query.keyword" clearable :placeholder="searchPlaceholder" style="width: 260px"
          @keyup.enter="load" :prefix-icon="Search" />

        <el-select v-if="props.module.resource === 'users'" v-model="query.role" placeholder="选择角色" clearable
          style="width: 150px" @change="load">
          <el-option label="管理员" value="ROLE_ADMIN" />
          <el-option label="健康管家" value="ROLE_MANAGER" />
          <el-option label="护士" value="ROLE_NURSE" />
          <el-option label="入住老人" value="ROLE_USER" />
        </el-select>

        <el-select v-if="props.module.resource === 'users'" v-model="query.enabled" placeholder="账号状态" clearable
          style="width: 130px" @change="load">
          <el-option label="正常" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>

        <el-button type="primary" @click="load" :loading="loading" :disabled="loading">搜索</el-button>
      </div>

      <div class="toolbar-right">
        <el-button v-if="!readOnly" type="primary" @click="openCreate" :disabled="loading">
          <el-icon><Plus /></el-icon>
          新增{{ module.title }}
        </el-button>
        <el-button @click="exportExcel" :loading="exportLoading" :disabled="loading">
          <el-icon><Download /></el-icon>
          导出Excel
        </el-button>
      </div>
    </div>

    <el-skeleton v-if="loading" :rows="6" animated class="table-skeleton" />

    <div class="table-scroll-wrap" v-else>
      <el-table :data="rows" stripe border :cell-style="{ padding: '10px 6px', textAlign: 'center' }"
        :header-cell-style="{
          background: '#f5faf8', color: '#244b42', fontWeight: 600,
          textAlign: 'center', whiteSpace: 'nowrap', padding: '12px 6px'
        }" class="data-table" @row-click="openDetail">

        <el-table-column label="序号" width="70" align="center">
          <template #default="{ $index }">
            {{ (query.current - 1) * query.size + $index + 1 }}
          </template>
        </el-table-column>

        <!-- 图片列 -->
        <el-table-column v-if="hasImageField" :label="imageField.label" align="center" width="100">
          <template #default="{ row }">
            <el-image v-if="row[imageField.prop]" :src="row[imageField.prop]" fit="cover" class="meal-table-img"
              preview-teleported />
            <span v-else class="no-img-tip">暂无图片</span>
          </template>
        </el-table-column>

        <!-- 后端自动生成的编号等额外列 -->
        <el-table-column v-for="col in extraDisplayColumns" :key="'ex-' + col.prop" :label="col.label"
          :min-width="getWidth(col.prop)" align="center">
          <template #default="{ row }">{{ row[col.prop] ?? '-' }}</template>
        </el-table-column>

        <!-- 表单字段列 -->
        <el-table-column v-for="field in filterFields" :key="field.prop" :label="field.label"
          :min-width="getWidth(field.prop)" align="center">
          <template #default="{ row }">
            <span v-if="field.prop === 'role'">{{ roleFormatter(row.role) }}</span>
            <span v-else-if="field.prop === 'enabled'">{{ enabledFormatter(row.enabled) }}</span>
            <span v-else-if="field.prop === 'checkedIn'">{{ checkedInFormatter(row.checkedIn) }}</span>
            <span v-else-if="field.prop === 'serviceId'">{{ row.serviceName || '-' }}</span>
            <span v-else-if="field.prop === 'customerId'">{{ row.customerName || '-' }}</span>
            <span v-else-if="field.prop === 'bedId'">{{ row.bedNo || '-' }}</span>
            <span v-else-if="field.prop === 'levelId'">{{ row.levelName || '-' }}</span>
            <span v-else-if="field.prop === 'itemId'">{{ row.itemName || '-' }}</span>
            <span v-else-if="field.prop === 'managerId'">{{ row.managerName || '-' }}</span>
            <span v-else>{{ row[field.prop] ?? '-' }}</span>
          </template>
        </el-table-column>

        <el-table-column label="操作" fixed="right" :width="readOnly ? 100 : (props.module.resource === 'outings' || props.module.resource === 'nursing-records' ? 300 : 220)" align="center">
          <template #default="{ row }">
            <el-button size="small" @click.stop="openDetail(row)">详情</el-button>
            <el-button v-if="props.module.resource === 'outings' && row.status === '外出中'" size="small" type="success" @click.stop="confirmReturn(row)">确认返回</el-button>
            <el-button v-if="props.module.resource === 'nursing-records' && row.result !== '已完成'" size="small" type="success" @click.stop="confirmNursing(row)">确认完成</el-button>
            <el-button v-if="!readOnly" size="small" type="primary" @click.stop="openEdit(row)">编辑</el-button>
            <el-button v-if="!readOnly" size="small" type="danger" @click.stop="remove(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-pagination v-model:current-page="query.current" v-model:page-size="query.size" background
      layout="total, sizes, prev, pager, next, jumper" :total="total" class="pagination custom-pagination"
      @current-change="load" :page-sizes="[10, 20, 50]" @size-change="onSizeChange" />
  </el-card>

  <!-- 新增/编辑弹窗 -->
  <el-dialog v-model="dialogVisible" :title="form.id ? '编辑' : '新增'" width="680px" :close-on-click-modal="false">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px" label-position="right">
      <el-form-item v-for="field in fields" :key="field.prop" :label="field.label" :prop="field.prop">
        <el-upload v-if="field.type === 'image'" :action="uploadUrl" :headers="uploadHeaders"
          :before-upload="beforeImgUpload" :on-success="handleImgSuccess" :on-error="handleImgError" :on-remove="handleImgRemove"
          accept="image/jpeg,image/png,image/gif,image/webp"
          list-type="picture-card" :file-list="imgList">
          <el-icon>
            <Upload />
          </el-icon>
          <template #tip>支持 jpg/png，最大 5MB</template>
        </el-upload>
        <el-input v-else-if="field.type === 'password'" v-model="form[field.prop]" type="password" show-password
          clearable placeholder="请输入密码（不修改请留空）" />
        <el-input v-else-if="field.type === 'text'" v-model="form[field.prop]" clearable />
        <el-input v-else-if="field.type === 'textarea'" v-model="form[field.prop]" type="textarea" :rows="3"
          clearable />
        <el-input-number v-else-if="field.type === 'number'" v-model="form[field.prop]" :min="0" style="width: 100%"
          controls-position="right" />
        <el-date-picker v-else-if="field.type === 'date'" v-model="form[field.prop]" value-format="YYYY-MM-DD"
          type="date" style="width: 100%" placeholder="选择日期" />
        <el-date-picker v-else-if="field.type === 'datetime'" v-model="form[field.prop]"
          value-format="YYYY-MM-DD HH:mm:ss" type="datetime" style="width: 100%" placeholder="选择日期时间" />
        <el-select v-else-if="field.type === 'select'" v-model="form[field.prop]" style="width: 100%" clearable>
          <el-option v-for="opt in field.options" :key="getOptionValue(opt)" :label="getOptionLabel(opt)"
            :value="getOptionValue(opt)" :disabled="opt.disabled === true" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="save" :loading="submitLoading">保存</el-button>
      </div>
    </template>
  </el-dialog>

  <!-- 详情抽屉 -->
  <el-drawer v-model="detailVisible" title="详情" size="520px" :close-on-click-modal="false" class="detail-drawer">
    <div class="detail-header">
      <div class="detail-avatar">{{ avatarChar }}</div>
      <div class="detail-header-text">
        <h3>{{ headerTitle }}</h3>
        <span class="detail-sub" v-if="detail.role">{{ roleFormatter(detail.role) }}</span>
        <span class="detail-sub" v-else-if="detail.mealDate">{{ detail.mealDate }}</span>
      </div>
    </div>

    <el-descriptions :column="1" border :label-style="{ width: '130px', fontWeight: 600 }" class="detail-descriptions">
      <!-- 额外列（编号等） -->
      <el-descriptions-item v-for="col in extraDisplayColumns" :key="'dex-' + col.prop" :label="col.label">
        {{ detail[col.prop] ?? '-' }}
      </el-descriptions-item>

      <!-- 表单字段 -->
      <el-descriptions-item v-for="field in fields.filter(f => f.prop !== 'password')" :key="field.prop"
        :label="field.label">
        <template v-if="field.type === 'image'">
          <div class="detail-img-wrap">
            <el-image v-if="detail[field.prop]" :src="detail[field.prop]" fit="cover" class="detail-img"
              preview-teleported />
            <div v-else class="detail-img-empty">
              <el-icon :size="28" color="#ccc">
                <Picture />
              </el-icon>
              <span>暂无图片</span>
            </div>
          </div>
        </template>
        <template v-else-if="field.prop === 'role'">{{ roleFormatter(detail.role) }}</template>
        <template v-else-if="field.prop === 'enabled'">
          <el-tag :type="detail.enabled === 1 ? 'success' : 'danger'" size="small" round>{{
            enabledFormatter(detail.enabled) }}</el-tag>
        </template>
        <template v-else-if="field.prop === 'checkedIn'">
          <el-tag :type="detail.checkedIn === 1 ? 'success' : 'info'" size="small" round>{{
            checkedInFormatter(detail.checkedIn) }}</el-tag>
        </template>
        <template v-else-if="field.prop === 'customerId'">{{ detail.customerName || '-' }}</template>
        <template v-else-if="field.prop === 'bedId'">{{ detail.bedNo || '-' }}</template>
        <template v-else-if="field.prop === 'serviceId'">{{ detail.serviceName || '-' }}</template>
        <template v-else-if="field.prop === 'managerId'">{{ detail.managerName || '-' }}</template>
        <template v-else-if="field.prop === 'levelId'">{{ detail.levelName || '-' }}</template>
        <template v-else-if="field.prop === 'itemId'">{{ detail.itemName || '-' }}</template>
        <template v-else>{{ detail[field.prop] ?? '-' }}</template>
      </el-descriptions-item>
    </el-descriptions>
  </el-drawer>
</template>

<script setup>
import { computed, onMounted, onUnmounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Upload, Picture, Search, Download } from '@element-plus/icons-vue'
import { createApi, deleteApi, pageApi, updateApi, outingReturnApi, nursingConfirmApi, exportApi } from '../api/crud'
import { useUserStore } from '../stores/user'
import {
  toFields, roleFormatter, enabledFormatter, checkedInFormatter,
  searchHints, dropdownSources, getExtraColumns
} from './moduleConfig'

const userStore = useUserStore()
const props = defineProps({ module: { type: Object, required: true } })

// 老人（ROLE_USER）只读：隐藏新增/编辑/删除按钮
const readOnly = computed(() => userStore.role === 'ROLE_USER')

const searchPlaceholder = computed(() => searchHints[props.module.resource] || '输入关键字搜索')

const extraDisplayColumns = computed(() => getExtraColumns(props.module.resource))

function getOptionValue(opt) {
  if (opt !== null && typeof opt === 'object') return opt.value
  return opt
}
function getOptionLabel(opt) {
  if (opt !== null && typeof opt === 'object') return opt.label
  return opt
}

// ===================== 通用下拉框数据加载（带缓存） =====================
const dropdownData = ref({})
const dropdownLoaded = ref(false)        // 是否已加载过
const destroyed = ref(false)

const loadAllDropdowns = async () => {
  if (dropdownLoaded.value || destroyed.value) return

  const sources = dropdownSources[props.module.resource]
  if (!sources) { dropdownData.value = {}; dropdownLoaded.value = true; return }

  dropdownLoaded.value = true  // 先标记，防止重复加载
  const results = {}

  // 外出登记模块：先获取当前外出中的客户ID列表
  let outingCustomerIds = new Set()
  if (props.module.resource === 'outings') {
    try {
      const outingRes = await pageApi('outings', { current: 1, size: 500 })
      outingCustomerIds = new Set((outingRes.records || []).filter(o => o.status === '外出中').map(o => o.customerId))
    } catch (err) {
      console.error('加载外出中客户列表失败', err)
    }
  }

  const tasks = sources.map(async (config) => {
    try {
      const params = { current: 1, size: 500 }
      if (config.params) Object.assign(params, config.params)
      const res = await pageApi(config.resource, params)
      if (destroyed.value) return
      results[config.prop] = (res.records || []).map(item => {
        const opt = {
          label: item[config.label],
          value: item[config.value]
        }
        // 标记禁用项：入住登记时禁用已入住客户和已占用床位，退住/外出时禁用未入住客户
        if (config.filterField && item[config.filterField] === config.filterValue) {
          opt.disabled = true
          opt.label = `${item[config.label]}（${config.filterLabel}）`
        }
        // 外出登记：标记当前外出中的客户为禁用
        if (props.module.resource === 'outings' && config.prop === 'customerId' && outingCustomerIds.has(item.id)) {
          opt.disabled = true
          opt.label = `${item[config.label]}（外出中）`
        }
        return opt
      })
    } catch (err) {
      console.error(`加载 [${config.prop}] 下拉数据失败`, err)
      results[config.prop] = []
    }
  })

  await Promise.all(tasks)
  if (destroyed.value) return
  dropdownData.value = results
}

// ===================== 字段配置 =====================
const fields = computed(() => {
  const f = toFields(props.module.resource)
  f.forEach(field => {
    if (dropdownData.value[field.prop]) {
      field.options = dropdownData.value[field.prop]
    }
  })
  return f
})

const imageField = computed(() => fields.value.find(f => f.type === 'image'))
const hasImageField = computed(() => !!imageField.value)

const filterFields = computed(() => {
  const extraProps = extraDisplayColumns.value.map(c => c.prop)
  return fields.value.filter(f => {
    if (f.prop === 'password') return false
    if (f.type === 'image') return false
    if (extraProps.includes(f.prop)) return false
    if (props.module.resource === 'customers' && f.prop === 'remark') return false
    if (props.module.resource === 'meals' && f.prop === 'specialNeed') return false
    return true
  })
})

// ===================== 查询 & 数据 =====================
const query = reactive({
  current: 1, size: 10, keyword: '',
  sort: 'createTime,desc', role: '', enabled: null
})

const rows = ref([])
const total = ref(0)
const loading = ref(false)
const submitLoading = ref(false)
const exportLoading = ref(false)

const dialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref()
const form = reactive({})
const detail = ref({})

const avatarChar = computed(() => {
  const d = detail.value
  const name = d.name || d.customerName || d.mealNo || d.registerNo || d.checkoutNo
    || d.outingNo || d.serviceName || d.levelName || d.itemName || d.areaName || d.recordNo || '?'
  return name.toString().charAt(0)
})

const headerTitle = computed(() => {
  const d = detail.value
  return d.name || d.customerName || d.mealNo || d.registerNo || d.checkoutNo
    || d.outingNo || d.serviceName || d.levelName || d.itemName || d.areaName || d.recordNo || '未知'
})

const uploadUrl = '/api/upload/file'
const uploadHeaders = computed(() => ({ Authorization: `Bearer ${userStore.token}` }))
const imgList = ref([])

const beforeImgUpload = (file) => {
  const isImage = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'].includes(file.type)
  if (!isImage) { ElMessage.error('仅支持 jpg/png/gif/webp 格式图片'); return false }
  if (file.size > 5 * 1024 * 1024) { ElMessage.error('图片大小不能超过 5MB'); return false }
  return true
}

const handleImgSuccess = (res) => {
  const url = typeof res === 'string' ? res : (res?.data || res)
  if (imageField.value && url) form[imageField.value.prop] = url
  if (url) imgList.value = [{ url }]
}

const handleImgError = (err) => {
  ElMessage.error('图片上传失败，请检查网络或文件格式')
  console.error('图片上传失败', err)
}

const handleImgRemove = () => {
  if (imageField.value) form[imageField.value.prop] = ''
  imgList.value = []
}

const getWidth = (prop) => {
  const widthMap = {
    customerNo: 110, registerNo: 110, checkoutNo: 110, outingNo: 110, mealNo: 110, recordNo: 110,
    customerName: 100, name: 90, gender: 80, age: 80, phone: 130,
    emergencyContact: 120, checkInDate: 110, healthStatus: 120, checkedIn: 100,
    breakfast: 120, lunch: 120, dinner: 120, mealDate: 110, mealImg: 110,
    outTime: 160, expectedReturnTime: 160, actualReturnTime: 160, status: 100,
    role: 120, enabled: 100, bedId: 100, managerId: 120,
    operator: 100, serviceId: 120, serviceName: 120
  }
  return widthMap[prop] || 100
}

const rules = computed(() => {
  const ruleEntries = fields.value
    .filter(f => f.required)
    .map(f => {
      if (f.prop === 'password') {
        return [f.prop, [{ required: !form.id, message: `请填写${f.label}`, trigger: 'blur' }]]
      }
      return [f.prop, [{ required: true, message: `请填写${f.label}`, trigger: 'blur' }]]
    })
  return Object.fromEntries(ruleEntries)
})

function defaultValue(field) {
  if (field.default !== null && field.default !== undefined) return field.default
  return field.type === 'number' ? 0 : ''
}

function resetForm(row = {}) {
  Object.keys(form).forEach(key => delete form[key])

  if (imageField.value && row[imageField.value.prop]) {
    imgList.value = [{ url: row[imageField.value.prop] }]
  } else {
    imgList.value = []
  }

  fields.value.forEach(field => {
    if (field.prop === 'password' && row.id) { form[field.prop] = ''; return }
    form[field.prop] = row[field.prop] ?? defaultValue(field)
  })
  form.id = row.id ?? undefined
}

function onSizeChange() {
  query.current = 1
  load()
}

async function load() {
  if (destroyed.value) return
  loading.value = true
  try {
    const res = await pageApi(props.module.resource, query)
    if (destroyed.value) return
    rows.value = res.records || []
    total.value = res.total || 0
  } catch (err) {
    if (!destroyed.value) {
      ElMessage.error('数据加载失败')
      console.error(err)
    }
  } finally {
    if (!destroyed.value) loading.value = false
  }
}

// ★ 改动点：新增/编辑时才加载下拉数据（每次打开弹窗都刷新）
async function openCreate() {
  resetForm()
  dropdownLoaded.value = false  // 重置缓存，强制重新加载
  await loadAllDropdowns()
  dialogVisible.value = true
}

async function openEdit(row) {
  resetForm(row)
  dropdownLoaded.value = false  // 重置缓存，强制重新加载
  await loadAllDropdowns()
  dialogVisible.value = true
}

function openDetail(row) {
  detail.value = { ...row }
  detailVisible.value = true
}

async function save() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch { return }
  submitLoading.value = true
  try {
    const submitData = { ...form }
    if (submitData.id && !submitData.password) delete submitData.password
    form.id
      ? await updateApi(props.module.resource, form.id, submitData)
      : await createApi(props.module.resource, submitData)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    load()
  } catch (err) {
    const msg = err?.response?.data?.message || '保存失败'
    ElMessage.error(msg)
    console.error(err)
  } finally {
    submitLoading.value = false
  }
}

async function remove(row) {
  try { await ElMessageBox.confirm('确定删除本条数据？', '提示', { type: 'warning', confirmButtonText: '确定', cancelButtonText: '取消' }) } catch { return }
  loading.value = true
  try {
    await deleteApi(props.module.resource, row.id)
    ElMessage.success('删除成功')
    await load()
  } catch (err) {
    ElMessage.error('删除失败')
  }
}

async function confirmReturn(row) {
  try { await ElMessageBox.confirm(`确认 ${row.customerName || '该客户'} 已返回？`, '确认返回', { type: 'info', confirmButtonText: '确认返回', cancelButtonText: '取消' }) } catch { return }
  loading.value = true
  try {
    await outingReturnApi(row.id)
    ElMessage.success('已确认返回')
    await load()
  } catch (err) {
    ElMessage.error(err?.response?.data?.message || '确认返回失败')
  }
}

async function confirmNursing(row) {
  try { await ElMessageBox.confirm(`确认 ${row.customerName || '该客户'} 的护理已完成？`, '确认完成', { type: 'info', confirmButtonText: '确认完成', cancelButtonText: '取消' }) } catch { return }
  loading.value = true
  try {
    await nursingConfirmApi(row.id, '已完成')
    ElMessage.success('已确认完成')
    await load()
  } catch (err) {
    ElMessage.error(err?.response?.data?.message || '确认失败')
  }
}

async function exportExcel() {
  exportLoading.value = true
  try {
    const res = await exportApi(props.module.resource, query)
    // res 是 blob 数据，检查是否真的是 Excel 文件
    if (res && res.type === 'application/json') {
      // 后端返回了 JSON 错误而不是 Excel
      const text = await res.text()
      try {
        const json = JSON.parse(text)
        ElMessage.error(json.message || '没有可导出的数据')
      } catch { ElMessage.error('导出失败') }
      return
    }
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${props.module.title || props.module.resource}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (err) {
    ElMessage.error(err?.response?.data?.message || '导出失败，请重试')
  } finally {
    exportLoading.value = false
  }
}

// ★ 改动点：onMounted 只加载表格数据，不加载下拉数据
onMounted(() => {
  load()
})

// ★ 改动点：切换模块时重置下拉缓存，只加载表格数据
watch(() => props.module.resource, async () => {
  query.keyword = ''
  query.role = ''
  query.enabled = null
  query.current = 1
  dropdownData.value = {}
  dropdownLoaded.value = false          // 切换模块时重置下拉缓存
  load()
})

// 组件销毁时标记，防止异步操作更新已销毁的组件
onUnmounted(() => { destroyed.value = true })
</script>

<style scoped>
.page-card {
  border: 0;
  border-radius: 22px;
  padding: 24px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.toolbar-left {
  display: flex;
  gap: 12px;
  align-items: center;
}

.toolbar-right {
  display: flex;
  gap: 12px;
  align-items: center;
}

.table-skeleton {
  margin-bottom: 20px;
}

.table-scroll-wrap {
  overflow-x: auto;
  width: 100%;
  margin-bottom: 10px;
  max-height: calc(100vh - 360px);
}

.data-table {
  border-radius: 12px;
  overflow: hidden;
  width: 100% !important;
}

:deep(.el-table--border) {
  border: 1px solid #e8f0ed;
}

:deep(.el-table--border .el-table__cell) {
  border-right: 1px solid #e8f0ed;
  border-bottom: 1px solid #e8f0ed;
  white-space: nowrap !important;
}

.meal-table-img {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  cursor: pointer;
  border: 1px solid #e8f0ed;
  box-shadow: 0 2px 4px #00000010;
}

.no-img-tip {
  color: #999;
  font-size: 12px;
}

:deep(.el-table--striped .el-table__row--striped td) {
  background: #f8fbf9;
}

:deep(.el-table__row:hover td) {
  background: #f5faf8 !important;
}

:deep(.el-input__wrapper) {
  border-radius: 12px;
  border: 1.5px solid #d5e5e0;
  box-shadow: none !important;
  background: #fff;
  transition: border-color 0.25s ease, box-shadow 0.25s ease;
}

:deep(.el-input__wrapper:hover) {
  border-color: #8abfb0;
}

:deep(.el-input__wrapper.is-focus) {
  border-color: #2f6f62;
  box-shadow: 0 0 0 3px rgba(47, 111, 98, 0.1) !important;
}

:deep(.el-input__prefix) {
  color: #a0b5ae;
}

:deep(.el-select .el-input__wrapper) {
  border-radius: 12px;
  border: 1.5px solid #d5e5e0;
  box-shadow: none !important;
}

:deep(.el-select .el-input__wrapper:hover) {
  border-color: #8abfb0;
}

:deep(.el-select .el-input.is-focus .el-input__wrapper) {
  border-color: #2f6f62;
  box-shadow: 0 0 0 3px rgba(47, 111, 98, 0.1) !important;
}

:deep(.el-date-editor .el-input__wrapper) {
  border-radius: 12px;
  border: 1.5px solid #d5e5e0;
  box-shadow: none !important;
}

:deep(.el-input-number .el-input__wrapper) {
  border-radius: 12px;
  border: 1.5px solid #d5e5e0;
  box-shadow: none !important;
}

.custom-pagination {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 4px;
}

.custom-pagination :deep(.el-pagination__total) {
  color: #6b8a7f;
  font-size: 13px;
  margin-right: 12px;
}

.custom-pagination :deep(.el-pagination__sizes) {
  margin-right: 12px;
}

.custom-pagination :deep(.el-pagination__sizes .el-input__wrapper) {
  border-radius: 10px;
  border: 1.5px solid #d5e5e0;
  box-shadow: none !important;
  height: 30px;
}

.custom-pagination :deep(.btn-prev),
.custom-pagination :deep(.btn-next) {
  border-radius: 10px !important;
  border: 1.5px solid #d5e5e0 !important;
  background: #fff !important;
  color: #5a7a70 !important;
  min-width: 32px;
  height: 32px;
  transition: all 0.2s ease;
}

.custom-pagination :deep(.btn-prev:hover:not(:disabled)),
.custom-pagination :deep(.btn-next:hover:not(:disabled)) {
  border-color: #2f6f62 !important;
  color: #2f6f62 !important;
  background: rgba(47, 111, 98, 0.04) !important;
}

.custom-pagination :deep(.btn-prev:disabled),
.custom-pagination :deep(.btn-next:disabled) {
  opacity: 0.4;
  cursor: not-allowed;
}

.custom-pagination :deep(.el-pager) {
  display: flex;
  gap: 4px;
}

.custom-pagination :deep(.el-pager li) {
  border-radius: 10px !important;
  border: 1.5px solid #d5e5e0 !important;
  background: #fff !important;
  color: #5a7a70 !important;
  font-weight: 500;
  min-width: 32px;
  height: 32px;
  transition: all 0.2s ease;
}

.custom-pagination :deep(.el-pager li:hover) {
  border-color: #2f6f62 !important;
  color: #2f6f62 !important;
  background: rgba(47, 111, 98, 0.04) !important;
}

.custom-pagination :deep(.el-pager li.is-active) {
  background: #2f6f62 !important;
  border-color: #2f6f62 !important;
  color: #fff !important;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(47, 111, 98, 0.25);
}

.custom-pagination :deep(.el-pagination__jump) {
  margin-left: 12px;
  color: #6b8a7f;
  font-size: 13px;
}

.custom-pagination :deep(.el-pagination__jump .el-input__wrapper) {
  border-radius: 10px;
  border: 1.5px solid #d5e5e0;
  box-shadow: none !important;
  width: 50px;
  height: 30px;
}

:deep(.el-dialog),
:deep(.el-drawer) {
  border-radius: 22px;
}

:deep(.el-dialog__header),
:deep(.el-drawer__header) {
  padding: 24px 24px 16px;
  font-size: 18px;
  font-weight: 600;
  color: #244b42;
}

:deep(.el-dialog__body),
:deep(.el-drawer__body) {
  padding: 0 24px 24px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e8f0ed;
}

.detail-avatar {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: linear-gradient(135deg, #2f6f62, #4a9e8f);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(47, 111, 98, 0.25);
}

.detail-header-text h3 {
  margin: 0 0 4px;
  font-size: 18px;
  font-weight: 700;
  color: #244b42;
  line-height: 1.3;
}

.detail-sub {
  font-size: 13px;
  color: #7a9e95;
  background: #f0f7f5;
  padding: 2px 10px;
  border-radius: 20px;
}

.detail-descriptions {
  border-radius: 12px;
  overflow: hidden;
}

:deep(.el-descriptions--border .el-descriptions__cell) {
  border: 1px solid #e8f0ed;
  padding: 14px 16px;
}

:deep(.el-descriptions__label) {
  background: #f5faf8;
  color: #244b42;
  font-size: 14px;
}

:deep(.el-descriptions__content) {
  color: #3a3a3a;
  font-size: 14px;
  line-height: 1.6;
}

.detail-img-wrap {
  width: 100%;
  display: flex;
  justify-content: center;
}

.detail-img {
  width: 260px;
  height: 180px;
  border-radius: 14px;
  border: 1px solid #e8f0ed;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  cursor: pointer;
  transition: transform 0.25s ease, box-shadow 0.25s ease;
}

.detail-img:hover {
  transform: scale(1.03);
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
}

.detail-img-empty {
  width: 260px;
  height: 180px;
  border-radius: 14px;
  border: 2px dashed #d5e5e0;
  background: #f8fbf9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #b5c8c2;
  font-size: 13px;
}

:deep(.el-button--primary) {
  background: #2f6f62;
  border-color: #2f6f62;
  border-radius: 12px;
}

:deep(.el-button--primary:hover) {
  background: #275d52 !important;
  border-color: #275d52 !important;
}

:deep(.el-button--danger) {
  border-radius: 12px;
}

:deep(.el-button--small) {
  border-radius: 10px;
}

:deep(.data-table .el-table__cell > .cell) {
  white-space: nowrap !important;
  overflow: hidden;
  text-overflow: ellipsis;
}
</style>

<style>
.el-message {
  border-radius: 14px !important;
  border: none !important;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08) !important;
  padding: 14px 20px !important;
  font-size: 14px !important;
  font-weight: 500;
}

.el-message--success {
  background: #f0f7f5 !important;
  color: #244b42 !important;
  border-left: 4px solid #2f6f62 !important;
}

.el-message--success .el-message__icon {
  color: #2f6f62 !important;
}

.el-message--error {
  background: #fdf0f0 !important;
  color: #8b3030 !important;
  border-left: 4px solid #d94444 !important;
}

.el-message--error .el-message__icon {
  color: #d94444 !important;
}

.el-message--warning {
  background: #fdf8ee !important;
  color: #8a7340 !important;
  border-left: 4px solid #e8a040 !important;
}

.el-message--warning .el-message__icon {
  color: #e8a040 !important;
}

.el-message--info {
  background: #f5f5f5 !important;
  color: #5a5a5a !important;
  border-left: 4px solid #909399 !important;
}

.el-message-box {
  border-radius: 22px !important;
  border: none !important;
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.12) !important;
  padding: 0 !important;
  overflow: hidden;
}

.el-message-box__header {
  padding: 20px 24px 8px !important;
}

.el-message-box__title {
  font-size: 17px !important;
  font-weight: 700 !important;
  color: #244b42 !important;
}

.el-message-box__content {
  padding: 8px 24px 20px !important;
  color: #5a7a70 !important;
  font-size: 14px !important;
}

.el-message-box__btns {
  padding: 12px 24px 20px !important;
  gap: 10px;
}

.el-message-box__btns .el-button {
  border-radius: 12px !important;
  min-width: 72px;
}

.el-message-box__btns .el-button--primary {
  background: #2f6f62 !important;
  border-color: #2f6f62 !important;
}

.el-message-box__btns .el-button--primary:hover {
  background: #275d52 !important;
  border-color: #275d52 !important;
}

.el-message-fade-enter-active {
  transition: all 0.35s cubic-bezier(0.22, 1, 0.36, 1) !important;
}

.el-message-fade-leave-active {
  transition: all 0.25s ease !important;
}

.el-message-fade-enter-from {
  transform: translateY(-16px) !important;
  opacity: 0 !important;
}
</style>
