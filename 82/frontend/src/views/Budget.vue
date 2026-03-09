<template>
  <div class="budget-page">
    <a-card>
      <template #title>
        <div style="display: flex; align-items: center; gap: 8px;">
        <h2>Budget</h2>
          <a-tooltip :title="getPageTooltip()" placement="top">
            <QuestionCircleOutlined style="color: #999; cursor: help;" />
          </a-tooltip>
        </div>
      </template>
      
      <!-- Total Budget Overview -->
      <div style="margin-top: 20px;">
        <div style="margin-bottom: 16px; padding: 12px; background: #f6f8fa; border-left: 4px solid #1890ff; border-radius: 4px;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <span style="font-weight: 600; color: #1890ff;">📊 Total Budget Overview</span>
            <a-tooltip title="This section shows your overall budget status including total budget, amount used, remaining budget, and usage percentage. Green indicates healthy usage, orange shows moderate usage, and red indicates high usage or overspending." placement="top">
              <QuestionCircleOutlined style="color: #999; cursor: help;" />
            </a-tooltip>
          </div>
          <p style="margin: 8px 0 0 0; color: #666; font-size: 14px;">
            Monitor your overall financial health with key budget metrics. Click the question mark for detailed explanations.
          </p>
        </div>
        <a-row :gutter="16">
          <a-col :span="6">
            <a-card size="small">
              <div style="text-align: center;">
                <div style="font-size: 14px; color: #666; margin-bottom: 4px;">Total Budget</div>
                <div style="font-size: 24px; font-weight: bold; color: #1890ff;">
                  ${{ budgetData.totalBudget.toLocaleString() }}
                </div>
              </div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card size="small">
              <div style="text-align: center;">
                <div style="font-size: 14px; color: #666; margin-bottom: 4px;">Used</div>
                <div style="font-size: 24px; font-weight: bold; color: #fa8c16;">
                  ${{ getTotalUsed().toLocaleString() }}
                </div>
              </div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card size="small">
              <div style="text-align: center;">
                <div style="font-size: 14px; color: #666; margin-bottom: 4px;">Money Left</div>
                <div style="font-size: 24px; font-weight: bold;" :style="{ color: getTotalBalance() < 0 ? '#ff4d4f' : '#52c41a' }">
                  ${{ getTotalBalance().toLocaleString() }}
                </div>
              </div>
            </a-card>
          </a-col>
          <a-col :span="6">
            <a-card size="small">
              <div style="text-align: center;">
                <div style="font-size: 14px; color: #666; margin-bottom: 4px;">Usage Rate</div>
                <div style="font-size: 24px; font-weight: bold;" :style="{ 
                  color: getTotalUsagePercentage() > 80 ? '#ff4d4f' : getTotalUsagePercentage() > 60 ? '#fa8c16' : '#52c41a' 
                }">
                  {{ getTotalUsagePercentage() }}%
                </div>
              </div>
            </a-card>
          </a-col>
        </a-row>
        
        <!-- Power of Attorney Budget Adjustment Button -->
        <div v-if="userRole === 'poa'" style="margin-top: 16px; text-align: right;">
          <div style="display: flex; align-items: center; gap: 8px; justify-content: flex-end; margin-bottom: 8px;">
            <span style="font-size: 12px; color: #666;">Adjust your budget settings</span>
            <a-tooltip title="Click this button to open the budget adjustment modal where you can modify total budget, reallocate funds between categories, or adjust sub-item budgets." placement="top">
              <QuestionCircleOutlined style="color: #999; cursor: help;" />
            </a-tooltip>
          </div>
          <a-button type="primary" @click="showBudgetAdjustModal">
            Edit Adjust Budget
          </a-button>
        </div>
      </div>
      
      <!-- Budget Management Table -->
      <div style="margin-top: 20px;">
        <div style="margin-bottom: 16px; padding: 12px; background: #f6f8fa; border-left: 4px solid #52c41a; border-radius: 4px;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <span style="font-weight: 600; color: #52c41a;">📋 Budget Management Table</span>
            <a-tooltip title="This table shows detailed budget breakdown by categories and sub-items. Click on any row to expand and view monthly usage details. Power of Attorney users can add new categories and sub-items using the buttons above." placement="top">
              <QuestionCircleOutlined style="color: #999; cursor: help;" />
            </a-tooltip>
          </div>
          <p style="margin: 8px 0 0 0; color: #666; font-size: 14px;">
            View and manage your budget by categories. Expand rows to see monthly spending details and sub-item breakdowns.
          </p>
        </div>
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px;">
          <div style="display: flex; align-items: center; gap: 12px;">
            <h3>Budget Management Overview</h3>
            <div v-if="getWarningInfo().length > 0" style="display: flex; align-items: center; gap: 8px;">
              <span style="background: #ff4d4f; color: white; padding: 2px 8px; border-radius: 12px; font-size: 12px; font-weight: bold;">
                {{ getWarningInfo().length }} Warning{{ getWarningInfo().length > 1 ? 's' : '' }}
              </span>
              <span style="color: #ff4d4f; font-size: 16px;">🚨</span>
            </div>
          </div>
          <div v-if="userRole === 'poa'">
            <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 8px;">
              <span style="font-size: 12px; color: #666;">Add new budget items</span>
              <a-tooltip title="Use these buttons to add new budget categories or sub-items. " placement="top">
                <QuestionCircleOutlined style="color: #999; cursor: help;" />
              </a-tooltip>
            </div>
            <a-button type="dashed" @click="showAddCategoryModal" style="margin-right: 8px;">
              + Add Category
            </a-button>
            <a-button type="dashed" @click="showAddSubElementModal">
              + Add Sub-element
            </a-button>
          </div>
        </div>
        <a-table 
          :dataSource="budgetData.categories" 
          :columns="budgetColumns"
          :pagination="false"
          rowKey="id"
          :expandRowByClick="true"
          :rowClassName="getRowClassName"
        >
          <template #expandedRowRender="{ record }">
            <a-table 
              :dataSource="record.subElements" 
              :columns="subElementColumns"
              :pagination="false"
              rowKey="id"
              size="small"
            >
              <template #expandedRowRender="{ record: subElement }">
                <div style="padding: 16px; background: #fafafa; border-radius: 6px;">
                  <div style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px;">
                    <h4 style="margin: 0;">{{ subElement.name }} - Monthly Usage Details</h4>
                    <a-tooltip title="This shows the monthly spending breakdown for this sub-item. Each card represents one month (Jan-Dec) with the amount spent. The summary below shows annual total, budget, and remaining balance." placement="top">
                      <QuestionCircleOutlined style="color: #999; cursor: help;" />
                    </a-tooltip>
                  </div>
                  <a-row :gutter="8">
                    <a-col v-for="(amount, index) in subElement.monthlyUsage" :key="index" :span="2">
                      <a-card size="small" style="text-align: center;">
                        <div style="font-size: 12px; color: #666;">{{ getMonthName(index) }}</div>
                        <div style="font-weight: bold; color: #1890ff;">${{ amount }}</div>
                      </a-card>
                    </a-col>
                  </a-row>
                  <div style="margin-top: 12px; padding: 8px; background: #e6f7ff; border-radius: 4px;">
                    <strong>Annual Total:</strong>${{ subElement.totalUtilised.toLocaleString() }} | 
                    <strong>Budget:</strong>${{ subElement.subElementBudget.toLocaleString() }} | 
                    <strong>Money Left:</strong>
                    <span :style="{ color: subElement.balance < 0 ? 'red' : 'green' }">
                      ${{ subElement.balance.toLocaleString() }}
                    </span>
                  </div>
                </div>
              </template>
            </a-table>
          </template>
        </a-table>
      </div>
      
      <!-- Budget Warning Information -->
      <div v-if="getWarningInfo().length > 0" style="margin-top: 20px;">
        <div style="margin-bottom: 16px; padding: 12px; background: #fff7e6; border-left: 4px solid #fa8c16; border-radius: 4px;">
          <div style="display: flex; align-items: center; gap: 8px;">
            <span style="font-weight: 600; color: #fa8c16;">⚠️ Budget Warnings</span>
            <a-tooltip title="This section displays budget alerts when spending reaches 80% or more of the allocated budget. Red alerts indicate critical overspending, while orange alerts show approaching budget limits. Take action to adjust budgets or control spending." placement="top">
              <QuestionCircleOutlined style="color: #999; cursor: help;" />
            </a-tooltip>
          </div>
          <p style="margin: 8px 0 0 0; color: #666; font-size: 14px;">
            Important budget alerts that require your attention. Review and take action on items approaching or exceeding budget limits.
          </p>
        </div>
        <h3>Budget Warnings</h3>
        <a-alert
          v-for="warning in getWarningInfo()"
          :key="`${warning.category}-${warning.subElement}`"
          :type="warning.level === 'critical' ? 'error' : 'warning'"
          :message="`${warning.category} - ${warning.subElement}`"
          :description="`Used ${warning.percentage}% (${warning.utilised}/${warning.budget}), Money Left: ${warning.balance}`"
          show-icon
          style="margin-bottom: 10px;"
        />
      </div>
    </a-card>

    <!-- First Visit Welcome Modal -->
    <a-modal
      v-model:open="firstVisitModalVisible"
      title="Welcome to Budget Page"
      :footer="null"
      :closable="false"
      :maskClosable="false"
      width="600px"
    >
      <div style="line-height: 1.6;">
        <div style="text-align: center; margin-bottom: 20px;">
          <div style="font-size: 48px; color: #1890ff; margin-bottom: 16px;">💰</div>
          <h3>Welcome to the Budget Page!</h3>
        </div>
        
        <p style="margin-bottom: 16px;">
          Here you can view budget overview, expense details, spending charts and warning information. If you have questions about any area, please click the question mark next to it.
        </p>
        
        <h4 style="color: #1890ff; margin-bottom: 12px;">Budget page includes:</h4>
        <ul style="margin-bottom: 16px;">
          <li><strong>Overview:</strong> Monthly budget total, used, remaining, percentage</li>
          <li><strong>Expense Details:</strong> Each expense with time, type, amount, status</li>
          <li><strong>Charts:</strong> Bar/pie charts showing categorized spending</li>
          <li><strong>Warning Alerts:</strong> For example, budgets over 85% will be highlighted</li>
        </ul>
        
        <h4 style="color: #fa8c16; margin-bottom: 12px;">Budget Management Features:</h4>
        <ul style="margin-bottom: 20px;">
          <li><strong>Category Budget:</strong> Manage annual budget by category</li>
          <li><strong>Sub-element Budget:</strong> Specific sub-elements under each category</li>
          <li><strong>Monthly Tracking:</strong> Usage for months 1-12</li>
          <li><strong>Smart Warnings:</strong> Automatic alerts when reaching 80%</li>
          <li><strong>Budget Adjustment:</strong> Support for fund reallocation</li>
        </ul>
        
        <div style="text-align: center;">
          <a-button type="primary" @click="closeFirstVisitModal" size="large">
            Start Using Budget Management
          </a-button>
        </div>
      </div>
    </a-modal>

    <!-- Budget Adjustment Modal -->
    <a-modal
      v-model:open="budgetAdjustModalVisible"
      title="Budget Adjustment"
      width="800px"
      @ok="handleBudgetAdjust"
      @cancel="cancelBudgetAdjust"
    >
      <div style="line-height: 1.6;">
        <a-alert
          message="Budget Adjustment Instructions"
          description="You can adjust the budget in the following ways: 1) Adjust total budget – increase or decrease the overall budget. 2) Reallocate between categories – move budget from one category to another. 3) Reallocate between sub-items – move budget from one sub-item to another within the same category."
          type="info"
          show-icon
          style="margin-bottom: 20px;"
        />
        
        <a-tabs v-model:activeKey="adjustTab">
          <a-tab-pane key="total" tab="Adjust Total Budget">
            <div style="padding: 20px;">
              <a-form :model="budgetAdjustForm" layout="vertical">
                <a-form-item label="Current Total Budget">
                  <a-input :value="`$${budgetData.totalBudget.toLocaleString()}`" disabled />
                </a-form-item>
                <a-form-item label="New Total Budget" required>
                  <a-input-number
                    v-model:value="budgetAdjustForm.newTotalBudget"
                    :min="0"
                    :step="1000"
                    style="width: 100%"
                    :formatter="(value) => `$${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                    :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
                  />
                </a-form-item>
                <a-form-item label="Adjustment Reason">
                  <a-textarea v-model:value="budgetAdjustForm.totalAdjustReason" placeholder="Please explain the reason for adjusting the total budget" />
                </a-form-item>
              </a-form>
            </div>
          </a-tab-pane>
          
          <a-tab-pane key="category" tab="Category Reallocation">
            <div style="padding: 20px;">
              <a-form :model="budgetAdjustForm" layout="vertical">
                <a-form-item label="From Category" required>
                  <a-select v-model:value="budgetAdjustForm.fromCategory" placeholder="Select category to reduce budget">
                    <a-select-option v-for="category in budgetData.categories" :key="category.id" :value="category.id">
                      {{ category.name }} (Current: ${{ category.categoryBudget.toLocaleString() }})
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="To Category" required>
                  <a-select v-model:value="budgetAdjustForm.toCategory" placeholder="Select category to increase budget">
                    <a-select-option v-for="category in budgetData.categories" :key="category.id" :value="category.id">
                      {{ category.name }} (Current: ${{ category.categoryBudget.toLocaleString() }})
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="Adjustment Amount" required>
                  <a-input-number
                    v-model:value="budgetAdjustForm.categoryAdjustAmount"
                    :min="0"
                    :step="1000"
                    style="width: 100%"
                    :formatter="(value) => `$${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                    :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
                  />
                </a-form-item>
                <a-form-item label="Adjustment Reason">
                  <a-textarea v-model:value="budgetAdjustForm.categoryAdjustReason" placeholder="Please explain the reason for inter-category adjustment" />
                </a-form-item>
              </a-form>
            </div>
          </a-tab-pane>
          
          <a-tab-pane key="subelement" tab="Sub-item Reallocation">
            <div style="padding: 20px;">
              <a-form :model="budgetAdjustForm" layout="vertical">
                <a-form-item label="Select Category" required>
                  <a-select v-model:value="budgetAdjustForm.selectedCategory" placeholder="Select category" @change="onCategoryChange">
                    <a-select-option v-for="category in budgetData.categories" :key="category.id" :value="category.id">
                      {{ category.name }}
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="From Sub-element" required>
                  <a-select v-model:value="budgetAdjustForm.fromSubElement" placeholder="Select sub-element to reduce budget">
                    <a-select-option v-for="subElement in getSelectedCategorySubElements()" :key="subElement.id" :value="subElement.id">
                      {{ subElement.name }} (Current: ${{ subElement.subElementBudget.toLocaleString() }})
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="To Sub-element" required>
                  <a-select v-model:value="budgetAdjustForm.toSubElement" placeholder="Select sub-element to increase budget">
                    <a-select-option v-for="subElement in getSelectedCategorySubElements()" :key="subElement.id" :value="subElement.id">
                      {{ subElement.name }} (Current: ${{ subElement.subElementBudget.toLocaleString() }})
                    </a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="Adjustment Amount" required>
                  <a-input-number
                    v-model:value="budgetAdjustForm.subElementAdjustAmount"
                    :min="0"
                    :step="1000"
                    style="width: 100%"
                    :formatter="(value) => `$${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                    :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
                  />
                </a-form-item>
                <a-form-item label="Adjustment Reason">
                  <a-textarea v-model:value="budgetAdjustForm.subElementAdjustReason" placeholder="Please explain the reason for sub-element adjustment" />
                </a-form-item>
              </a-form>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </a-modal>

    <!-- Add Category Modal -->
    <a-modal
      v-model:open="addCategoryModalVisible"
      title="Add New Category"
      @ok="handleAddCategory"
      @cancel="cancelAddCategory"
    >
      <a-form :model="addCategoryForm" layout="vertical">
        <a-form-item label="Category Name" required>
          <a-input v-model:value="addCategoryForm.name" placeholder="e.g.: Food & Groceries" />
        </a-form-item>
        <a-form-item label="Annual Budget" required>
          <a-input-number
            v-model:value="addCategoryForm.budget"
            :min="0"
            :step="1000"
            style="width: 100%"
            :formatter="(value) => `$${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
            :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
          />
        </a-form-item>
        <a-form-item label="Category Description">
          <a-textarea v-model:value="addCategoryForm.description" placeholder="Describe the purpose of this category" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Add Sub-element Modal -->
    <a-modal
      v-model:open="addSubElementModalVisible"
      title="Add New Sub-element"
      @ok="handleAddSubElement"
      @cancel="cancelAddSubElement"
    >
      <a-form :model="addSubElementForm" layout="vertical">
        <a-form-item label="Select Category" required>
          <a-select v-model:value="addSubElementForm.categoryId" placeholder="Select category to add sub-element">
            <a-select-option v-for="category in budgetData.categories" :key="category.id" :value="category.id">
              {{ category.name }}
            </a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="Sub-element Name" required>
          <a-input v-model:value="addSubElementForm.name" placeholder="e.g.: Breakfast Items" />
        </a-form-item>
        <a-form-item label="Sub-element Budget" required>
          <a-input-number
            v-model:value="addSubElementForm.budget"
            :min="0"
            :step="100"
            style="width: 100%"
            :formatter="(value) => `$${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
            :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
          />
        </a-form-item>
        <a-form-item label="Sub-element Description">
          <a-textarea v-model:value="addSubElementForm.description" placeholder="Describe the purpose of this sub-element" />
        </a-form-item>
      </a-form>
    </a-modal>

    <!-- Monthly Input Modal -->
    <a-modal
      v-model:open="monthlyInputModalVisible"
      title="Input Monthly Data"
      @ok="handleMonthlyInput"
      @cancel="cancelMonthlyInput"
      width="800px"
    >
      <div v-if="currentSubElement" style="line-height: 1.6;">
        <a-alert
          :message="`Input Monthly Spending for: ${currentSubElement.name}`"
          :description="`Budget: $${currentSubElement.subElementBudget.toLocaleString()} | Current Total Used: $${currentSubElement.totalUtilised.toLocaleString()}`"
          type="info"
          show-icon
          style="margin-bottom: 20px;"
        />
        
        <a-form :model="monthlyInputForm" layout="vertical">
          <a-form-item label="Monthly Spending Data (Jan - Dec)">
            <a-row :gutter="8">
              <a-col v-for="(amount, index) in monthlyInputForm.monthlyData" :key="index" :span="4">
                <a-form-item :label="getMonthName(index)" style="margin-bottom: 8px;">
                  <a-input-number
                    v-model:value="monthlyInputForm.monthlyData[index]"
                    :min="0"
                    :step="10"
                    style="width: 100%"
                    :formatter="(value) => `$${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
                    :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
                    placeholder="0"
                  />
                </a-form-item>
              </a-col>
            </a-row>
          </a-form-item>
        </a-form>
        
        <div style="margin-top: 16px; padding: 12px; background: #f6f8fa; border-radius: 4px;">
          <h4 style="margin: 0 0 8px 0; color: #1890ff;">Calculation Preview:</h4>
          <div style="font-size: 14px;">
            <div><strong>Current Total Used:</strong> ${{ currentSubElement.totalUtilised.toLocaleString() }}</div>
            <div><strong>New Total Used:</strong> ${{ monthlyInputForm.monthlyData.reduce((sum, amount) => sum + (amount || 0), 0).toLocaleString() }}</div>
            <div><strong>Budget:</strong> ${{ currentSubElement.subElementBudget.toLocaleString() }}</div>
            <div><strong>New Balance:</strong> 
              <span :style="{ color: (currentSubElement.subElementBudget - monthlyInputForm.monthlyData.reduce((sum, amount) => sum + (amount || 0), 0)) < 0 ? 'red' : 'green' }">
                ${{ (currentSubElement.subElementBudget - monthlyInputForm.monthlyData.reduce((sum, amount) => sum + (amount || 0), 0)).toLocaleString() }}
              </span>
            </div>
            <div><strong>New Usage Rate:</strong> 
              <span :style="{ 
                color: currentSubElement.subElementBudget > 0 ? 
                  (Math.round((monthlyInputForm.monthlyData.reduce((sum, amount) => sum + (amount || 0), 0) / currentSubElement.subElementBudget) * 100) > 80 ? 
                    (Math.round((monthlyInputForm.monthlyData.reduce((sum, amount) => sum + (amount || 0), 0) / currentSubElement.subElementBudget) * 100) >= 100 ? 'red' : 'orange') : 'green') : 'green'
              }">
                {{ currentSubElement.subElementBudget > 0 ? Math.round((monthlyInputForm.monthlyData.reduce((sum, amount) => sum + (amount || 0), 0) / currentSubElement.subElementBudget) * 100) : 0 }}%
              </span>
            </div>
          </div>
        </div>
      </div>
    </a-modal>

    <!-- Refund Modal -->
    <a-modal
      v-model:open="refundModalVisible"
      title="Process Refund"
      @ok="handleRefund"
      @cancel="cancelRefund"
      width="600px"
    >
      <div v-if="currentRefundItem" style="line-height: 1.6;">
        <a-alert
          message="Refund Information"
          :description="`Processing refund for: ${currentRefundItem.name} (Current Used: $${currentRefundItem.totalUtilised.toLocaleString()})`"
          type="info"
          show-icon
          style="margin-bottom: 20px;"
        />
        
        <a-form :model="refundForm" layout="vertical">
          <a-form-item label="Refund Amount" required>
            <a-input-number
              v-model:value="refundForm.amount"
              :min="0"
              :max="currentRefundItem.totalUtilised"
              :step="10"
              style="width: 100%"
              :formatter="(value) => `$${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')"
              :parser="(value) => value.replace(/\$\s?|(,*)/g, '')"
              placeholder="Enter refund amount"
            />
            <div style="font-size: 12px; color: #666; margin-top: 4px;">
              Maximum refundable: ${{ currentRefundItem.totalUtilised.toLocaleString() }}
            </div>
          </a-form-item>
          
          <a-form-item label="Refund Reason" required>
            <a-select v-model:value="refundForm.reason" placeholder="Select refund reason">
              <a-select-option value="defective">Defective product</a-select-option>
              <a-select-option value="wrong_brand">Wrong brand</a-select-option>
              <a-select-option value="wrong_size">Wrong size</a-select-option>
              <a-select-option value="quality_issue">Quality issue</a-select-option>
              <a-select-option value="duplicate_order">Duplicate order</a-select-option>
              <a-select-option value="customer_request">Customer request</a-select-option>
              <a-select-option value="other">Other reason</a-select-option>
            </a-select>
          </a-form-item>
          
          <a-form-item label="Additional Comments">
            <a-textarea 
              v-model:value="refundForm.comment" 
              placeholder="Add any additional details about this refund..."
              :rows="3"
            />
          </a-form-item>
        </a-form>
        
        <div style="margin-top: 16px; padding: 12px; background: #f6f8fa; border-radius: 4px;">
          <h4 style="margin: 0 0 8px 0; color: #1890ff;">Refund Impact Preview:</h4>
          <div v-if="refundForm.amount" style="font-size: 14px;">
            <div><strong>Current Used:</strong> ${{ currentRefundItem.totalUtilised.toLocaleString() }}</div>
            <div><strong>Refund Amount:</strong> ${{ refundForm.amount.toLocaleString() }}</div>
            <div><strong>New Used Amount:</strong> ${{ (currentRefundItem.totalUtilised - (refundForm.amount || 0)).toLocaleString() }}</div>
            <div><strong>New Money Left:</strong> ${{ (currentRefundItem.balance + (refundForm.amount || 0)).toLocaleString() }}</div>
          </div>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup>
import { ref, onMounted, h, resolveComponent } from 'vue'
import { QuestionCircleOutlined, UndoOutlined } from '@ant-design/icons-vue'
import { Button } from 'ant-design-vue'
import { useRouter } from 'vue-router'
import { Modal, message } from 'ant-design-vue'
import { getMe } from '@/services/userService'
import { 
  getBudgetByPatient,
  createBudget,
  updateBudget,
  adjustTotalBudget,
  deleteBudget,
  getBudgetCategories,
  createBudgetCategory,
  updateBudgetCategory,
  deleteBudgetCategory,
  getBudgetSubElements,
  createBudgetSubElement,
  updateBudgetSubElement,
  deleteBudgetSubElement,
  getBudgetCalculations,
  getBudgetReports
} from '@/services/budgetService'

const router = useRouter()
const userRole = ref('worker')
const firstVisitModalVisible = ref(false)
const budgetAdjustModalVisible = ref(false)
const adjustTab = ref('total')
const addCategoryModalVisible = ref(false)
const addSubElementModalVisible = ref(false)
const refundModalVisible = ref(false)
const currentRefundItem = ref(null)
const monthlyInputModalVisible = ref(false)
const currentSubElement = ref(null)

// Budget adjustment form
const budgetAdjustForm = ref({
  newTotalBudget: null,
  totalAdjustReason: '',
  fromCategory: null,
  toCategory: null,
  categoryAdjustAmount: null,
  categoryAdjustReason: '',
  selectedCategory: null,
  fromSubElement: null,
  toSubElement: null,
  subElementAdjustAmount: null,
  subElementAdjustReason: ''
})

// Add category form
const addCategoryForm = ref({
  name: '',
  budget: 0,
  description: ''
})

// Add sub-element form
const addSubElementForm = ref({
  categoryId: null,
  name: '',
  budget: 0,
  description: ''
})

// Refund form
const refundForm = ref({
  amount: null,
  reason: '',
  comment: ''
})

// Monthly input form
const monthlyInputForm = ref({
  monthlyData: new Array(12).fill(0)
})

onMounted(async () => {
  try {
    const userInfo = await getMe()
    userRole.value = userInfo?.data?.role || 'worker'
    
    // Load budget data from API
    await loadBudgetData()
    
    // Check if this is the first visit to budget page
    checkFirstVisit()
  } catch (e) {
    console.error('Failed to get user info:', e)
    // Keep using mock data if API fails
  }
})

// Load budget data from API
const loadBudgetData = async () => {
  try {
    const userInfo = await getMe()
    if (!userInfo?.data?.patientId) {
      console.warn('No patient ID found')
      return
    }
    
    // Load budget by patient
    const budgetResponse = await getBudgetByPatient(userInfo.data.patientId)
    if (budgetResponse?.data) {
      budgetData.value = budgetResponse.data
    }
    
    // Load budget categories
    const categoriesResponse = await getBudgetCategories(userInfo.data.patientId)
    if (categoriesResponse?.data) {
      budgetData.value.categories = categoriesResponse.data
    }
    
    // Load budget calculations
    const calculationsResponse = await getBudgetCalculations(userInfo.data.patientId)
    if (calculationsResponse?.data) {
      budgetData.value = {
        ...budgetData.value,
        ...calculationsResponse.data
      }
    }
    
  } catch (error) {
    console.error('Failed to load budget data:', error)
    // Show empty state if API fails
    budgetData.value = {
      totalBudget: 0,
      categories: []
    }
  }
}

// Check if this is the user's first visit to budget page
const checkFirstVisit = async () => {
  try {
    const userInfo = await getMe()
    const userId = userInfo?.data?.id || 'anonymous'
    const budgetFirstVisitKey = `budget_first_visit_${userId}`
    
    // Check if user has visited budget page before
    const hasVisitedBefore = localStorage.getItem(budgetFirstVisitKey)
    
    if (!hasVisitedBefore) {
      // First time visiting budget page
      firstVisitModalVisible.value = true
    }
  } catch (error) {
    console.error('Failed to check first visit:', error)
    // On error, don't show the modal to avoid annoying users
    firstVisitModalVisible.value = false
  }
}

// Close first visit modal and mark as visited
const closeFirstVisitModal = async () => {
  firstVisitModalVisible.value = false
  
  try {
    const userInfo = await getMe()
    const userId = userInfo?.data?.id || 'anonymous'
    const budgetFirstVisitKey = `budget_first_visit_${userId}`
    
    // Mark that user has visited budget page
    localStorage.setItem(budgetFirstVisitKey, 'true')
    console.log('Budget page first visit marked as completed for user:', userId)
  } catch (error) {
    console.error('Failed to save first visit status:', error)
  }
}

const getPageTooltip = () => {
  switch (userRole.value) {
    case 'manager':
      return 'Budget management page for viewing and managing financial budgets, expense statistics and financial reports'
    case 'poa':
      return 'Budget management page where Power of Attorney/Family Members can modify and manage budgets'
    default:
      return 'Budget management page'
  }
}

// Convert month index to month name
const getMonthName = (index) => {
  const months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
  return months[index] || 'Unknown'
}

// Budget data - loaded from API
const budgetData = ref({
  totalBudget: 0,
  categories: []
})

// Calculate warning level
const calculateWarningLevel = (utilised, budget) => {
  if (budget === 0) {
    return 'normal'
  }
  const percentage = (utilised / budget) * 100
  if (percentage >= 100) return 'critical'
  if (percentage >= 80) return 'warning'
  return 'normal'
}

// Get row class name for warning background
const getRowClassName = (record) => {
  const budget = Number(record?.categoryBudget ?? 0)
  const used = (record.subElements || []).reduce((s, it) => s + Number(it?.totalUtilised ?? 0), 0)
  const percentage = budget > 0 ? (used / budget) * 100 : 0
  
  if (percentage >= 100) {
    return 'warning-row-critical'
  } else if (percentage > 80) {
    return 'warning-row-warning'
  }
  return ''
}

// Get warning information
const getWarningInfo = () => {
  const warnings = []
  // Check if categories exist and is an array
  if (!budgetData.value?.categories || !Array.isArray(budgetData.value.categories)) {
    return warnings
  }
  
  budgetData.value.categories.forEach(category => {
    // Check if subElements exist and is an array
    if (!category?.subElements || !Array.isArray(category.subElements)) {
      return
    }
    
    category.subElements.forEach(subElement => {
      const percentage = subElement.subElementBudget > 0 ? (subElement.totalUtilised / subElement.subElementBudget) * 100 : 0
      if (percentage >= 80) {
        // Dynamically calculate warning level
        let level = 'warning'
        if (percentage >= 100) {
          level = 'critical'
        }
        
        warnings.push({
          category: category.name,
          subElement: subElement.name,
          percentage: Math.round(percentage),
          utilised: subElement.totalUtilised,
          budget: subElement.subElementBudget,
          balance: subElement.balance,
          level: level
        })
      }
    })
  })
  return warnings
}

// Calculate total used amount
const getTotalUsed = () => {
  if (!budgetData.value?.categories || !Array.isArray(budgetData.value.categories)) {
    return 0
  }
  
  return budgetData.value.categories.reduce((total, category) => {
    if (!category?.subElements || !Array.isArray(category.subElements)) {
      return total
    }
    
    return total + category.subElements.reduce((categoryTotal, subElement) => {
      return categoryTotal + (subElement.totalUtilised || 0)
    }, 0)
  }, 0)
}

// Calculate total balance
const getTotalBalance = () => {
  return budgetData.value.totalBudget - getTotalUsed()
}

// Calculate total usage percentage
const getTotalUsagePercentage = () => {
  if (budgetData.value.totalBudget === 0) {
    return 0
  }
  return Math.round((getTotalUsed() / budgetData.value.totalBudget) * 100)
}

// Show budget adjustment modal
const showBudgetAdjustModal = () => {
  budgetAdjustModalVisible.value = true
  // Reset form
  budgetAdjustForm.value = {
    newTotalBudget: budgetData.value.totalBudget,
    totalAdjustReason: '',
    fromCategory: null,
    toCategory: null,
    categoryAdjustAmount: null,
    categoryAdjustReason: '',
    selectedCategory: null,
    fromSubElement: null,
    toSubElement: null,
    subElementAdjustAmount: null,
    subElementAdjustReason: ''
  }
}

// Cancel budget adjustment
const cancelBudgetAdjust = () => {
  budgetAdjustModalVisible.value = false
}

// Handle budget adjustment
const handleBudgetAdjust = async () => {
  try {
    const userInfo = await getMe()
    if (!userInfo?.data?.patientId) {
      message.error('Patient ID not found')
      return
    }

    if (adjustTab.value === 'total') {
      // Adjust total budget
      if (budgetAdjustForm.value.newTotalBudget && budgetAdjustForm.value.newTotalBudget > 0) {
        // Call backend API to adjust total budget using dedicated endpoint
        const response = await adjustTotalBudget(
          userInfo.data.patientId,
          budgetAdjustForm.value.newTotalBudget,
          budgetAdjustForm.value.totalAdjustReason || 'Budget adjustment'
        )
        if (response?.data) {
          budgetData.value.totalBudget = budgetAdjustForm.value.newTotalBudget
          console.log('Total budget adjusted to:', budgetAdjustForm.value.newTotalBudget)
          console.log('Adjustment reason:', budgetAdjustForm.value.totalAdjustReason)
          message.success('Total budget updated successfully!')
        } else {
          message.error('Failed to update total budget')
          return
        }
      }
    } else if (adjustTab.value === 'category') {
      // Inter-category adjustment
      if (budgetAdjustForm.value.fromCategory && budgetAdjustForm.value.toCategory && 
          budgetAdjustForm.value.categoryAdjustAmount && budgetAdjustForm.value.categoryAdjustAmount > 0) {
        
        const fromCategory = budgetData.value.categories.find(c => c.id === budgetAdjustForm.value.fromCategory)
        const toCategory = budgetData.value.categories.find(c => c.id === budgetAdjustForm.value.toCategory)
        
        if (fromCategory && toCategory) {
          // Call backend API to update categories
          const fromCategoryData = {
            name: fromCategory.name,
            description: fromCategory.description,
            categoryBudget: fromCategory.categoryBudget - budgetAdjustForm.value.categoryAdjustAmount
          }
          const toCategoryData = {
            name: toCategory.name,
            description: toCategory.description,
            categoryBudget: toCategory.categoryBudget + budgetAdjustForm.value.categoryAdjustAmount
          }
          
          const fromResponse = await updateBudgetCategory(userInfo.data.patientId, fromCategory.id, fromCategoryData)
          const toResponse = await updateBudgetCategory(userInfo.data.patientId, toCategory.id, toCategoryData)
          
          if (fromResponse?.data && toResponse?.data) {
            fromCategory.categoryBudget -= budgetAdjustForm.value.categoryAdjustAmount
            toCategory.categoryBudget += budgetAdjustForm.value.categoryAdjustAmount
            console.log('Inter-category budget adjustment completed')
            console.log('Adjustment reason:', budgetAdjustForm.value.categoryAdjustReason)
            message.success('Category budget adjustment completed!')
          } else {
            message.error('Failed to update category budgets')
            return
          }
        }
      }
    } else if (adjustTab.value === 'subelement') {
      // Inter-sub-element adjustment
      if (budgetAdjustForm.value.selectedCategory && budgetAdjustForm.value.fromSubElement && 
          budgetAdjustForm.value.toSubElement && budgetAdjustForm.value.subElementAdjustAmount && 
          budgetAdjustForm.value.subElementAdjustAmount > 0) {
        
        const category = budgetData.value.categories.find(c => c.id === budgetAdjustForm.value.selectedCategory)
        if (category && category.subElements && Array.isArray(category.subElements)) {
          const fromSubElement = category.subElements.find(s => s.id === budgetAdjustForm.value.fromSubElement)
          const toSubElement = category.subElements.find(s => s.id === budgetAdjustForm.value.toSubElement)
          
          if (fromSubElement && toSubElement) {
            // Call backend API to update sub-elements
            const fromSubElementData = {
              name: fromSubElement.name,
              description: fromSubElement.description,
              subElementBudget: fromSubElement.subElementBudget - budgetAdjustForm.value.subElementAdjustAmount
            }
            const toSubElementData = {
              name: toSubElement.name,
              description: toSubElement.description,
              subElementBudget: toSubElement.subElementBudget + budgetAdjustForm.value.subElementAdjustAmount
            }
            
            const fromResponse = await updateBudgetSubElement(
              userInfo.data.patientId, 
              budgetAdjustForm.value.selectedCategory,
              fromSubElement.id,
              fromSubElementData
            )
            const toResponse = await updateBudgetSubElement(
              userInfo.data.patientId,
              budgetAdjustForm.value.selectedCategory,
              toSubElement.id,
              toSubElementData
            )
            
            if (fromResponse?.data && toResponse?.data) {
              fromSubElement.subElementBudget -= budgetAdjustForm.value.subElementAdjustAmount
              toSubElement.subElementBudget += budgetAdjustForm.value.subElementAdjustAmount
              console.log('Inter-sub-element budget adjustment completed')
              console.log('Adjustment reason:', budgetAdjustForm.value.subElementAdjustReason)
              message.success('Sub-element budget adjustment completed!')
            } else {
              message.error('Failed to update sub-element budgets')
              return
            }
          }
        }
      }
    }
    
    budgetAdjustModalVisible.value = false
  } catch (error) {
    console.error('Failed to adjust budget:', error)
    message.error('Failed to adjust budget. Please try again.')
  }
}

// Category selection change
const onCategoryChange = () => {
  budgetAdjustForm.value.fromSubElement = null
  budgetAdjustForm.value.toSubElement = null
}

// Get selected category sub-elements
const getSelectedCategorySubElements = () => {
  if (!budgetAdjustForm.value.selectedCategory) return []
  const category = budgetData.value.categories.find(c => c.id === budgetAdjustForm.value.selectedCategory)
  return category?.subElements || []
}

// Show add category modal
const showAddCategoryModal = () => {
  addCategoryModalVisible.value = true
  addCategoryForm.value = {
    name: '',
    budget: 0,
    description: ''
  }
}

// Show add sub-element modal
const showAddSubElementModal = () => {
  addSubElementModalVisible.value = true
  addSubElementForm.value = {
    categoryId: null,
    name: '',
    budget: 0,
    description: ''
  }
}

// Handle add category
const handleAddCategory = async () => {
  if (addCategoryForm.value.name && addCategoryForm.value.budget && addCategoryForm.value.budget > 0) {
    try {
      const userInfo = await getMe()
      if (!userInfo?.data?.patientId) {
        message.error('Patient ID not found')
        return
      }

      // Call backend API to create category
      // Backend expects 'categoryBudget' not 'budget'
      const categoryData = {
        name: addCategoryForm.value.name,
        categoryBudget: addCategoryForm.value.budget,
        description: addCategoryForm.value.description
      }
      
      const response = await createBudgetCategory(userInfo.data.patientId, categoryData)
      if (response?.data) {
        // Add the new category to local data
        const newCategory = {
          id: response.data.id || Math.max(...budgetData.value.categories.map(c => c.id)) + 1,
          name: addCategoryForm.value.name,
          categoryBudget: addCategoryForm.value.budget,
          subElements: []
        }
        
        budgetData.value.categories.push(newCategory)
        console.log('New category added:', newCategory)
        
        addCategoryModalVisible.value = false
        message.success('Category added successfully!')
      } else {
        message.error('Failed to create category')
      }
    } catch (error) {
      console.error('Failed to create category:', error)
      message.error('Failed to create category. Please try again.')
    }
  }
}

// Cancel add category
const cancelAddCategory = () => {
  addCategoryModalVisible.value = false
}

// Handle add sub-element
const handleAddSubElement = async () => {
  if (addSubElementForm.value.categoryId && addSubElementForm.value.name && 
      addSubElementForm.value.budget && addSubElementForm.value.budget > 0) {
    
    try {
      const userInfo = await getMe()
      if (!userInfo?.data?.patientId) {
        message.error('Patient ID not found')
        return
      }

          const category = budgetData.value.categories.find(c => c.id === addSubElementForm.value.categoryId)
      if (category) {
        // Ensure subElements array exists
        if (!category.subElements) {
          category.subElements = []
        }
        // Call backend API to create sub-element
        // Backend expects 'subElementBudget' not 'budget'
        const subElementData = {
          name: addSubElementForm.value.name,
          subElementBudget: addSubElementForm.value.budget,
          description: addSubElementForm.value.description
        }
        
        const response = await createBudgetSubElement(userInfo.data.patientId, addSubElementForm.value.categoryId, subElementData)
        if (response?.data) {
          // Add the new sub-element to local data
          const newSubElement = {
            id: response.data.id || Math.max(...budgetData.value.categories.flatMap(c => c.subElements).map(s => s.id)) + 1,
            name: addSubElementForm.value.name,
            subElementBudget: addSubElementForm.value.budget,
            monthlyUsage: new Array(12).fill(0),
            totalUtilised: 0,
            balance: addSubElementForm.value.budget,
            comments: addSubElementForm.value.description || 'Newly added sub-element',
            warningLevel: 'normal'
          }
          
          category.subElements.push(newSubElement)
          console.log('New sub-element added:', newSubElement)
          
          addSubElementModalVisible.value = false
          message.success('Sub-element added successfully!')
        } else {
          message.error('Failed to create sub-element')
        }
      }
    } catch (error) {
      console.error('Failed to create sub-element:', error)
      message.error('Failed to create sub-element. Please try again.')
    }
  }
}

// Cancel add sub-element
const cancelAddSubElement = () => {
  addSubElementModalVisible.value = false
}

// Show refund modal
const showRefundModal = (subElement) => {
  currentRefundItem.value = subElement
  refundModalVisible.value = true
  // Reset form
  refundForm.value = {
    amount: null,
    reason: '',
    comment: ''
  }
}

// Cancel refund
const cancelRefund = () => {
  refundModalVisible.value = false
  currentRefundItem.value = null
}

// Show monthly input modal
const showMonthlyInputModal = (subElement) => {
  currentSubElement.value = subElement
  monthlyInputModalVisible.value = true
  // Initialize form with current monthly data
  monthlyInputForm.value.monthlyData = [...(subElement.monthlyUsage || new Array(12).fill(0))]
}

// Cancel monthly input
const cancelMonthlyInput = () => {
  monthlyInputModalVisible.value = false
  currentSubElement.value = null
}

// Handle monthly input
const handleMonthlyInput = async () => {
  if (!currentSubElement.value) return
  
  try {
    const userInfo = await getMe()
    if (!userInfo?.data?.patientId) {
      message.error('Patient ID not found')
      return
    }

    // Find the category that contains this sub-element
    const category = budgetData.value.categories.find(cat => 
      cat.subElements && cat.subElements.some(sub => sub.id === currentSubElement.value.id)
    )
    
    if (!category || !category.id) {
      message.error('Category not found for this sub-element')
      return
    }

    // Calculate total utilised amount
    const totalUsed = monthlyInputForm.value.monthlyData.reduce((sum, amount) => sum + (amount || 0), 0)
    
    // Call backend API to update sub-element
    const subElementData = {
      id: currentSubElement.value.id,
      monthlyUsage: monthlyInputForm.value.monthlyData,
      totalUtilised: totalUsed,
      balance: currentSubElement.value.subElementBudget - totalUsed
    }
    
    const response = await updateBudgetSubElement(
      userInfo.data.patientId,
      category.id,
      currentSubElement.value.id,
      subElementData
    )
    if (response?.data) {
      // Update the sub-element's monthly usage data
      currentSubElement.value.monthlyUsage = [...monthlyInputForm.value.monthlyData]
      currentSubElement.value.totalUtilised = totalUsed
      
      // Calculate balance
      currentSubElement.value.balance = currentSubElement.value.subElementBudget - totalUsed
      
      // Update warning level
      const percentage = currentSubElement.value.subElementBudget > 0 ? (totalUsed / currentSubElement.value.subElementBudget) * 100 : 0
      if (percentage >= 100) {
        currentSubElement.value.warningLevel = 'critical'
      } else if (percentage >= 80) {
        currentSubElement.value.warningLevel = 'warning'
      } else {
        currentSubElement.value.warningLevel = 'normal'
      }
      
      // Update comments
      if (percentage >= 100) {
        currentSubElement.value.comments = 'Budget exhausted'
      } else if (percentage >= 80) {
        currentSubElement.value.comments = 'High usage - approaching budget limit'
      } else if (totalUsed > currentSubElement.value.subElementBudget) {
        currentSubElement.value.comments = 'Critical overspending - budget exceeded'
      } else {
        currentSubElement.value.comments = 'Normal usage'
      }
      
      console.log('Monthly data updated for:', currentSubElement.value.name, {
        monthlyData: monthlyInputForm.value.monthlyData,
        totalUsed,
        balance: currentSubElement.value.balance,
        percentage: Math.round(percentage)
      })
      
      monthlyInputModalVisible.value = false
      currentSubElement.value = null
      
      message.success('Monthly data updated successfully!')
    } else {
      message.error('Failed to update monthly data')
    }
  } catch (error) {
    console.error('Failed to update monthly data:', error)
    message.error('Failed to update monthly data. Please try again.')
  }
}

// Handle refund
const handleRefund = async () => {
  if (!refundForm.value.amount || refundForm.value.amount <= 0) {
    return
  }
  
  if (!refundForm.value.reason) {
    return
  }
  
  if (refundForm.value.amount > currentRefundItem.value.totalUtilised) {
    return
  }
  
  try {
    const userInfo = await getMe()
    if (!userInfo?.data?.patientId) {
      message.error('Patient ID not found')
      return
    }

    const refundAmount = refundForm.value.amount
    
    // Save old values before updating (for logging)
    const oldUsed = currentRefundItem.value.totalUtilised
    const oldBalance = currentRefundItem.value.balance
    
    // Find the category that contains this sub-element
    const category = budgetData.value.categories.find(cat => 
      cat.subElements && cat.subElements.some(sub => sub.id === currentRefundItem.value.id)
    )
    
    if (!category || !category.id) {
      message.error('Category not found for this sub-element')
      return
    }
    
    // Call backend API to update sub-element
    const subElementData = {
      id: currentRefundItem.value.id,
      totalUtilised: currentRefundItem.value.totalUtilised - refundAmount,
      balance: currentRefundItem.value.balance + refundAmount
    }
    
    const response = await updateBudgetSubElement(
      userInfo.data.patientId,
      category.id,
      currentRefundItem.value.id,
      subElementData
    )
    
    if (response?.data) {
      // Update used amount and balance
      currentRefundItem.value.totalUtilised -= refundAmount
      currentRefundItem.value.balance += refundAmount
      
      // Update warning level
      const newPercentage = currentRefundItem.value.subElementBudget > 0 ? (currentRefundItem.value.totalUtilised / currentRefundItem.value.subElementBudget) * 100 : 0
      if (newPercentage >= 100) {
        currentRefundItem.value.warningLevel = 'critical'
      } else if (newPercentage >= 80) {
        currentRefundItem.value.warningLevel = 'warning'
      } else {
        currentRefundItem.value.warningLevel = 'normal'
      }
      
      // Create refund comment
      const reasonText = {
        'defective': 'Defective product',
        'wrong_brand': 'Wrong brand',
        'wrong_size': 'Wrong size',
        'quality_issue': 'Quality issue',
        'duplicate_order': 'Duplicate order',
        'customer_request': 'Customer request',
        'other': 'Other reason'
      }[refundForm.value.reason] || refundForm.value.reason
      
      const refundComment = `Refund: $${refundAmount.toLocaleString()} - ${reasonText}${refundForm.value.comment ? ` (${refundForm.value.comment})` : ''}`
      
      // Update comments
      if (currentRefundItem.value.comments && currentRefundItem.value.comments !== 'Newly added sub-element') {
        currentRefundItem.value.comments += `; ${refundComment}`
      } else {
        currentRefundItem.value.comments = refundComment
      }
      
      console.log('Refund processed:', {
        subElement: currentRefundItem.value.name,
        refundAmount,
        oldUsed,
        newUsed: currentRefundItem.value.totalUtilised,
        oldBalance,
        newBalance: currentRefundItem.value.balance,
        reason: reasonText,
        comment: refundForm.value.comment
      })
      
      // Close modal
      refundModalVisible.value = false
      currentRefundItem.value = null
      
      // Show success message
      message.success(`Refund of $${refundAmount.toLocaleString()} processed successfully!`)
      
      // Ask if user wants to upload refund receipt (only on success)
      Modal.confirm({
        title: 'Upload Refund Receipt?',
        content: 'Would you like to upload the refund receipt for this transaction?',
        okText: 'Yes, Upload',
        cancelText: 'No, Thanks',
        onOk() {
          router.push('/app/upload')
        },
        onCancel() {
          console.log('User chose not to upload receipt')
        }
      })
    } else {
      message.error('Failed to process refund')
    }
  } catch (error) {
    console.error('Failed to process refund:', error)
    message.error('Failed to process refund. Please try again.')
  }
}

// Main table: Category summary (does not depend on totalUsed/totalBalance/usagePercentage written back in onMounted)
const budgetColumns = [
  { title: 'Category', dataIndex: 'name', key: 'name' },
  {
    title: 'Category Total Budget',
    dataIndex: 'categoryBudget',
    key: 'categoryBudget',
    align: 'right',
    customRender: ({ text }) => `$${Number(text ?? 0).toLocaleString()}`
  },
  {
    title: 'Used Amount',
    key: 'totalUsed',
    align: 'right',
    customRender: ({ record }) => {
      const used = (record.subElements || []).reduce((s, it) => s + Number(it?.totalUtilised ?? 0), 0)
      return `$${used.toLocaleString()}`
    }
  },
  {
    title: 'Money Left',
    key: 'totalBalance',
    align: 'right',
    customRender: ({ record }) => {
      const budget = Number(record?.categoryBudget ?? 0)
      const used = (record.subElements || []).reduce((s, it) => s + Number(it?.totalUtilised ?? 0), 0)
      return `$${(budget - used).toLocaleString()}`
    }
  },
  {
    title: 'Usage Rate',
    key: 'usagePercentage',
    align: 'right',
    customRender: ({ record }) => {
      const budget = Number(record?.categoryBudget ?? 0)
      const used = (record.subElements || []).reduce((s, it) => s + Number(it?.totalUtilised ?? 0), 0)
      const pct = budget > 0 ? Math.round((used / budget) * 100) : 0
      
      // Set color based on usage rate
      let color = '#52c41a' // Green - less than 80%
      if (pct >= 100) {
        color = '#ff4d4f' // Red - greater than or equal to 100%
      } else if (pct > 80) {
        color = '#fa8c16' // Yellow - greater than 80% but less than 100%
      }
      
      return h('span', { style: { color, fontWeight: 'bold' } }, `${pct}%`)
    }
  }
]

// Sub-table: Sub-element details (all values with fallback)
const subElementColumns = [
  { title: 'Sub-element', dataIndex: 'name', key: 'name' },
  {
    title: 'Budget',
    dataIndex: 'subElementBudget',
    key: 'subElementBudget',
    align: 'right',
    customRender: ({ text }) => `$${Number(text ?? 0).toLocaleString()}`
  },
  {
    title: 'Used',
    dataIndex: 'totalUtilised',
    key: 'totalUtilised',
    align: 'right',
    customRender: ({ text }) => `$${Number(text ?? 0).toLocaleString()}`
  },
  {
    title: 'Money Left',
    dataIndex: 'balance',
    key: 'balance',
    align: 'right',
    customRender: ({ text }) => `$${Number(text ?? 0).toLocaleString()}`
  },
  {
    title: 'Usage Rate',
    key: 'usagePercentage',
    align: 'right',
    customRender: ({ record }) => {
      const budget = Number(record?.subElementBudget ?? 0)
      const used = Number(record?.totalUtilised ?? 0)
      const pct = budget > 0 ? Math.round((used / budget) * 100) : 0
      
      // Set color based on usage rate
      let color = '#52c41a' // Green - less than 80%
      if (pct >= 100) {
        color = '#ff4d4f' // Red - greater than or equal to 100%
      } else if (pct > 80) {
        color = '#fa8c16' // Yellow - greater than 80% but less than 100%
      }
      
      return h('span', { style: { color, fontWeight: 'bold' } }, `${pct}%`)
    }
  },
  {
    title: 'Status',
    dataIndex: 'warningLevel',
    key: 'warningLevel',
    customRender: ({ text, record }) => {
      const budget = Number(record?.subElementBudget ?? 0)
      const used = Number(record?.totalUtilised ?? 0)
      const percentage = budget > 0 ? Math.round((used / budget) * 100) : 0
      
      let status = 'Normal'
      let icon = '✅'
      let color = '#52c41a'
      
      if (percentage > 100) {
        status = 'Over budget'
        icon = '🚨'
        color = '#ff4d4f'
      } else if (percentage === 100) {
        status = 'Fully used'
        icon = '🔴'
        color = '#ff4d4f'
      } else if (percentage > 80) {
        status = 'Warning'
        icon = '⚠️'
        color = '#fa8c16'
      }
      
      return h('div', { style: { display: 'flex', alignItems: 'center', gap: '4px' } }, [
        h('span', { style: { fontSize: '16px' } }, icon),
        h('span', { style: { color, fontWeight: 'bold' } }, status)
      ])
    }
  },
  { title: 'Comments', dataIndex: 'comments', key: 'comments' },
  {
    title: 'Monthly Usage',
    key: 'monthlyUsage',
    customRender: ({ record }) => {
      const arr = Array.isArray(record?.monthlyUsage) ? record.monthlyUsage : []
      const total = arr.reduce((s, n) => s + Number(n ?? 0), 0)
      const avg = arr.length ? Math.round(total / arr.length) : 0
      const max = arr.length ? Math.max(...arr) : 0
      const maxMonth = arr.length ? arr.indexOf(max) : -1
      const maxMonthName = maxMonth >= 0 ? getMonthName(maxMonth) : '-'
      return `Total: $${total.toLocaleString()} | Average: $${avg} | Highest: $${max}(${maxMonthName})`
    }
  },
  {
    title: 'Actions',
    key: 'actions',
    width: 200,
    customRender: ({ record }) => {
      // Only show actions for manager role
      if (userRole.value !== 'manager') {
        return h('span', { style: { color: '#999', fontStyle: 'italic' } }, 'View only')
      }
      
      return h('div', { style: { display: 'flex', gap: '8px', flexWrap: 'wrap' } }, [
        h('div', { style: { display: 'flex', alignItems: 'center', gap: '4px' } }, [
          h(Button, {
            type: 'primary',
            size: 'small',
            onClick: () => showMonthlyInputModal(record),
            style: {
              fontWeight: 'bold',
              minWidth: '120px'
            }
          }, () => 'Input Monthly Data'),
          h(
            resolveComponent('a-tooltip'),
            { title: 'Click to input monthly spending data for this sub-element. This will automatically calculate total used amount.' },
            {
              default: () => h('span', {
                style: {
                  color: '#999',
                  cursor: 'help',
                  fontSize: '12px',
                  border: '1px solid #999',
                  borderRadius: '50%',
                  width: '16px',
                  height: '16px',
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }
              }, '?')
            }
          )
        ]),
        h('div', { style: { display: 'flex', alignItems: 'center', gap: '4px' } }, [
          h(Button, {
            type: 'default',
            size: 'small',
            onClick: () => showRefundModal(record),
            style: {
              fontWeight: 'bold',
              minWidth: '80px'
            }
          }, () => 'Refund'),
          h(
            resolveComponent('a-tooltip'),
            { title: 'Click to process a refund for this item. You can return money to the budget and add refund reason.' },
            {
              default: () => h('span', {
                style: {
                  color: '#999',
                  cursor: 'help',
                  fontSize: '12px',
                  border: '1px solid #999',
                  borderRadius: '50%',
                  width: '16px',
                  height: '16px',
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }
              }, '?')
            }
          )
        ]),
        h('div', { style: { display: 'flex', alignItems: 'center', gap: '4px' } }, [
          h(Button, {
            type: 'default',         
            size: 'small',
            onClick: () => router.push('/app/upload'),
            style: {
              fontWeight: 'bold',
              minWidth: '100px'
            }
          }, () => 'Upload receipt'),
          h(
            resolveComponent('a-tooltip'),
            { title: 'Click to upload receipts or documents related to this budget item.' },
            {
              default: () => h('span', {
                style: {
                  color: '#999',
                  cursor: 'help',
                  fontSize: '12px',
                  border: '1px solid #999',
                  borderRadius: '50%',
                  width: '16px',
                  height: '16px',
                  display: 'inline-flex',
                  alignItems: 'center',
                  justifyContent: 'center'
                }
              }, '?')
            }
          )
        ])
      ])
    }
  }
]
</script>

<style scoped>
.budget-page {
  max-width: 1200px;
  margin: 0 auto;
}

/* Warning row background colors */
:deep(.warning-row-warning) {
  background-color: #fff7e6 !important;
  border-left: 4px solid #fa8c16 !important;
}

:deep(.warning-row-critical) {
  background-color: #fff2f0 !important;
  border-left: 4px solid #ff4d4f !important;
}

:deep(.warning-row-warning:hover) {
  background-color: #fff1d6 !important;
}

:deep(.warning-row-critical:hover) {
  background-color: #ffe7e6 !important;
}
</style>